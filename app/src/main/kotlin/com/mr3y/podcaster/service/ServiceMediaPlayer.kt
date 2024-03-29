package com.mr3y.podcaster.service

import androidx.annotation.OptIn
import androidx.annotation.VisibleForTesting
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.core.model.CurrentlyPlayingEpisode
import com.mr3y.podcaster.core.model.PlayingStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds

/**
 * Encapsulate media session's player operations to make it testable & isolated from Service class.
 */
@OptIn(UnstableApi::class)
class ServiceMediaPlayer (
    private val player: Player,
    private val podcastsRepository: PodcastsRepository,
) : ForwardingPlayer(player) {

    @VisibleForTesting
    internal val currentlyPlayingEpisode = MutableStateFlow<CurrentlyPlayingEpisode?>(null)

    fun startListeningForUpdatesIn(scope: CoroutineScope) {
        scope.launch {
            podcastsRepository.getCurrentlyPlayingEpisode().collectLatest { currentEpisode ->
                currentlyPlayingEpisode.update { currentEpisode }

                currentlyPlayingEpisode.value?.let { (episode, _, speed) ->
                    val duration = player.duration
                    // episode's durationInSec is sometimes reported as an approximate value,
                    // so we update it to match the exact value of the content duration.
                    if (duration != C.TIME_UNSET && episode.durationInSec?.toLong() != (duration / 1000)) {
                        podcastsRepository.updateEpisodeDuration((duration / 1000).toInt(), episode.id)
                        podcastsRepository.updateCurrentlyPlayingEpisodeSpeed(speed)
                    }
                }
                while (true) {
                    val episodeId = player.currentMediaItem?.mediaId?.toLongOrNull() ?: break
                    val progressInSec = player.currentPosition.div(1000)
                    if (progressInSec != 0L && player.isPlaying) {
                        currentlyPlayingEpisode.value?.let { (episode, _, _) ->
                            if (!episode.isCompleted && episode.durationInSec?.toLong() == progressInSec) {
                                podcastsRepository.markEpisodeAsCompleted(episode.id)
                            }
                        }

                        podcastsRepository.updateEpisodePlaybackProgress(progressInSec.toInt(), episodeId)
                    }
                    delay(1.seconds)
                }
            }
        }
    }

    fun attachPlayerListener() {
        player.apply {
            addListener(
                object : Player.Listener {
                    // Sync state between session & our app UI
                    override fun onPlayWhenReadyChanged(
                        playWhenReady: Boolean,
                        reason: Int,
                    ) {
                        val playingStatus = when {
                            playWhenReady -> PlayingStatus.Playing
                            playbackState == Player.STATE_BUFFERING -> PlayingStatus.Loading
                            else -> PlayingStatus.Paused
                        }
                        currentlyPlayingEpisode.value?.let { (episode, _, _) ->
                            val isAboutToPlay = playingStatus == PlayingStatus.Loading || playingStatus == PlayingStatus.Playing
                            episode.durationInSec?.let {
                                val hasReachedEndOfEpisode = abs((it * 1000).toLong() - currentPosition) <= 1000L && episode.progressInSec == episode.durationInSec
                                if (isAboutToPlay && hasReachedEndOfEpisode) {
                                    seekTo(0L)
                                    podcastsRepository.updateEpisodePlaybackProgress(progressInSec = 0, episodeId = episode.id)
                                }
                            }
                        }

                        podcastsRepository.updateCurrentlyPlayingEpisodeStatus(playingStatus)
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO || reason == Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT) {
                            currentlyPlayingEpisode.value?.let { (episode, _, _) ->
                                if (!episode.isCompleted) {
                                    podcastsRepository.markEpisodeAsCompleted(episode.id)
                                }
                            }
                        }

                        if (mediaItem != null) {
                            val nextEpisode = podcastsRepository.getEpisodeFromQueue(mediaItem.mediaId.toLong())
                            currentlyPlayingEpisode.value?.let { (currentEpisode, playingStatus, playingSpeed) ->
                                if (currentEpisode.id != nextEpisode.id) {
                                    val hasReachedEndOfEpisode = nextEpisode.durationInSec?.let { dur ->
                                        nextEpisode.progressInSec?.let { progress -> (dur - progress) <= 1 } ?: false
                                    } ?: false
                                    val position = if (hasReachedEndOfEpisode) {
                                        podcastsRepository.updateEpisodePlaybackProgress(progressInSec = 0, episodeId = nextEpisode.id)
                                        0L
                                    } else {
                                        nextEpisode.progressInSec?.times(1000)?.toLong() ?: 0L
                                    }
                                    podcastsRepository.setCurrentlyPlayingEpisode(CurrentlyPlayingEpisode(nextEpisode, playingStatus, playingSpeed))
                                    seekTo(position)
                                }
                            }
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        // TODO: log the error for better investigation
                        podcastsRepository.updateCurrentlyPlayingEpisodeStatus(PlayingStatus.Error)
                    }
                },
            )
        }
    }

}
