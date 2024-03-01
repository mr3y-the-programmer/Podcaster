package com.mr3y.podcaster.service

import android.content.Intent
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.audio.MediaCodecAudioRenderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ConnectionResult
import androidx.media3.session.MediaSession.ConnectionResult.AcceptedResultBuilder
import androidx.media3.session.MediaSession.MediaItemsWithStartPosition
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.ListenableFuture
import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.core.model.CurrentlyPlayingEpisode
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.PlayingStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.guava.future
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds

@OptIn(UnstableApi::class)
@AndroidEntryPoint
class PlaybackService : MediaSessionService() {

    @Inject
    lateinit var podcastsRepository: PodcastsRepository

    private val serviceScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var mediaSession: MediaSession? = null
    private val currentlyPlayingEpisode = MutableStateFlow<CurrentlyPlayingEpisode?>(null)

    override fun onCreate() {
        super.onCreate()
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
            .setUsage(C.USAGE_MEDIA)
            .build()
        val mediaSourceFactory = ProgressiveMediaSource.Factory(DownloadMediaService.buildCacheDataSourceFactory(this))
        val audioOnlyRenderersFactory = RenderersFactory { handler, _, audioListener, _, _ ->
            arrayOf(MediaCodecAudioRenderer(this, MediaCodecSelector.DEFAULT, handler, audioListener))
        }
        val player = ExoPlayer.Builder(this, audioOnlyRenderersFactory, mediaSourceFactory)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()
        mediaSession = MediaSession.Builder(this, player)
            .setCallback(PlaybackMediaSessionCallback())
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        attachPlayerListener()
        startListeningForUpdates()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startListeningForUpdates() {
        serviceScope.launch {
            podcastsRepository.getCurrentlyPlayingEpisode().collectLatest { currentEpisode ->
                currentlyPlayingEpisode.update { currentEpisode }
                val player = mediaSession?.player
                currentlyPlayingEpisode.value?.let { (episode, _, speed) ->
                    val duration = player?.duration ?: return@let
                    // episode's durationInSec is sometimes reported as an approximate value,
                    // so we update it to match the exact value of the content duration.
                    if (duration != C.TIME_UNSET && episode.durationInSec?.toLong() != (duration / 1000)) {
                        podcastsRepository.updateEpisodeDuration((duration / 1000).toInt(), episode.id)
                        podcastsRepository.updateCurrentlyPlayingEpisodeSpeed(speed)
                    }
                }
                while (true) {
                    val episodeId = player?.currentMediaItem?.mediaId?.toLongOrNull() ?: break
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

    private fun attachPlayerListener() {
        (mediaSession?.player as ExoPlayer).apply {
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

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player ?: return
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    private inner class PlaybackMediaSessionCallback : MediaSession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
        ): MediaSession.ConnectionResult {
            return AcceptedResultBuilder(session)
                .setAvailableSessionCommands(ConnectionResult.DEFAULT_SESSION_COMMANDS)
                .setAvailablePlayerCommands(ConnectionResult.DEFAULT_PLAYER_COMMANDS)
                .build()
        }

        override fun onPlaybackResumption(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
        ): ListenableFuture<MediaItemsWithStartPosition> {
            return serviceScope.future {
                val mediaItems = podcastsRepository.getQueueEpisodes().map { episode ->
                    MediaItem.Builder()
                        .setMediaId(episode.id.toString())
                        .setMediaMetadata(buildMetadataForEpisode(episode))
                        .setUri(Uri.Builder().encodedPath(episode.enclosureUrl).build())
                        .build()
                }
                val currentlyPlayingEpisode = podcastsRepository.getCurrentlyPlayingEpisodeNonObservable()

                if (currentlyPlayingEpisode != null) {
                    mediaSession.player.setPlaybackSpeed(currentlyPlayingEpisode.playingSpeed)
                }
                val startingPosition = currentlyPlayingEpisode?.episode?.progressInSec?.times(1000)?.toLong() ?: C.TIME_UNSET

                MediaItemsWithStartPosition(
                    mediaItems,
                    C.INDEX_UNSET,
                    startingPosition,
                )
            }
        }

        private fun buildMetadataForEpisode(episode: Episode): MediaMetadata {
            return MediaMetadata.Builder()
                .setTitle(episode.title)
                .setArtist(episode.podcastTitle)
                .setIsBrowsable(false)
                .setIsPlayable(true)
                .setArtworkUri(Uri.parse(episode.artworkUrl))
                .setMediaType(MediaMetadata.MEDIA_TYPE_PODCAST_EPISODE)
                .build()
        }
    }
}
