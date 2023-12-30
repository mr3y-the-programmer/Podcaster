package com.mr3y.podcaster.ui.presenter

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.core.model.CurrentlyPlayingEpisode
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.PlayingStatus
import com.mr3y.podcaster.service.PlaybackService
import com.mr3y.podcaster.ui.presenter.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@OptIn(UnstableApi::class)
class PodcasterAppState @Inject constructor(
    private val podcastsRepository: PodcastsRepository,
    @ApplicationScope private val applicationScope: CoroutineScope
) {

    private var controller: MediaController? = null
    private lateinit var controllerFuture: ListenableFuture<MediaController>

    val currentlyPlayingEpisode = podcastsRepository.getCurrentlyPlayingEpisode()
        .onEach {
            if (it != null) {
                val (episode, playingStatus, playingSpeed) = it
                _trackProgress.update { episode.progressInSec ?: 0 }
                controller?.apply {
                    val uri = Uri.Builder()
                        .encodedPath(episode.enclosureUrl)
                        .build()
                    val mediaMetadata = MediaMetadata.Builder()
                        .setTitle(episode.title)
                        .setArtist(episode.podcastTitle)
                        .setIsBrowsable(false)
                        .setIsPlayable(true)
                        .setArtworkUri(Uri.parse(episode.artworkUrl))
                        .setMediaType(MediaMetadata.MEDIA_TYPE_PODCAST_EPISODE)
                        .build()
                    val mediaItem = MediaItem.fromUri(uri)
                        .buildUpon()
                        .setMediaId(episode.id.toString())
                        .setMediaMetadata(mediaMetadata)
                        .build()
                    setMediaItem(mediaItem, episode.progressInSec?.times(1000)?.toLong() ?: 0L)
                    if (playingStatus == PlayingStatus.Playing || playingStatus == PlayingStatus.Loading) {
                        prepare()
                        setPlaybackSpeed(playingSpeed)
                        seekToAndPlay(episode.progressInSec)
                        if (playingStatus == PlayingStatus.Loading) {
                            podcastsRepository.updateCurrentlyPlayingEpisodeStatus(PlayingStatus.Playing)
                        }
                    }
                }
            }
        }
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    private val _isPlayerViewExpanded = MutableStateFlow(false)
    val isPlayerViewExpanded = _isPlayerViewExpanded.asStateFlow()

    private val _trackProgress = MutableStateFlow(0)
    val trackProgress = _trackProgress.asStateFlow()

    init {
        applicationScope.launch {
            while (true) {
                val progressInSec = controller?.currentPosition?.div(1000)
                if (progressInSec != null && progressInSec != 0L && controller?.isPlaying == true) {
                    _trackProgress.update { progressInSec.toInt() }
                }
                delay(1.seconds)
            }
        }
    }

    fun initializePlayer(context: Context) {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
            controller = controllerFuture.get()
                .apply {
                    addListener(
                        object : Player.Listener {
                            // Sync state between controller & our app UI
                            override fun onPlayWhenReadyChanged(
                                playWhenReady: Boolean,
                                reason: Int
                            ) {
                                val playingStatus = when {
                                    playbackState == Player.STATE_BUFFERING -> PlayingStatus.Loading
                                    playWhenReady -> PlayingStatus.Playing
                                    else -> PlayingStatus.Paused
                                }
                                podcastsRepository.updateCurrentlyPlayingEpisodeStatus(playingStatus)
                            }

                            override fun onPlayerError(error: PlaybackException) {
                                // TODO: log the error for better investigation
                                podcastsRepository.updateCurrentlyPlayingEpisodeStatus(PlayingStatus.Error)
                            }
                        }
                    )
                    val episodePlayingStatus = currentlyPlayingEpisode.value?.playingStatus
                    if ((episodePlayingStatus == PlayingStatus.Playing || episodePlayingStatus == PlayingStatus.Loading) && !isPlaying) {
                        // Trigger preparing the controller & playing the episode.
                        podcastsRepository.updateCurrentlyPlayingEpisodeStatus(PlayingStatus.Playing)
                    }
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    fun play(episode: Episode) {
        currentlyPlayingEpisode.value?.let { (currentEpisode, _, _) ->
            if (currentEpisode.id == episode.id) {
                resume()
                return
            }
        }
        val playbackSpeed = currentlyPlayingEpisode.value?.playingSpeed ?: 1.0f
        _trackProgress.update { episode.progressInSec ?: 0 }
        podcastsRepository.setCurrentlyPlayingEpisode(CurrentlyPlayingEpisode(episode, PlayingStatus.Loading, playbackSpeed))
    }

    fun resume() {
        currentlyPlayingEpisode.value?.let {
            seekToAndPlay(_trackProgress.value)
            podcastsRepository.updateEpisodePlaybackProgress(_trackProgress.value, it.episode.id)
            podcastsRepository.updateCurrentlyPlayingEpisodeStatus(PlayingStatus.Playing)
        }
    }

    fun pause() {
        controller?.pause()
        podcastsRepository.updateCurrentlyPlayingEpisodeStatus(PlayingStatus.Paused)
    }

    fun expandPlayerView() {
        _isPlayerViewExpanded.update { true }
    }

    fun collapsePlayerView() {
        _isPlayerViewExpanded.update { false }
    }

    fun changePlaybackSpeed(currentSpeed: Float): Float {
        return when(currentSpeed) {
            1.0f -> {
                applicationScope.launch {
                    podcastsRepository.updateCurrentlyPlayingEpisodeSpeed(1.5f)
                }
                controller?.setPlaybackSpeed(1.5f)
                1.5f
            }
            1.5f -> {
                applicationScope.launch {
                    podcastsRepository.updateCurrentlyPlayingEpisodeSpeed(2.0f)
                }
                controller?.setPlaybackSpeed(2.0f)
                2.0f
            }
            2.0f -> {
                applicationScope.launch {
                    podcastsRepository.updateCurrentlyPlayingEpisodeSpeed(0.75f)
                }
                controller?.setPlaybackSpeed(0.75f)
                0.75f
            }
            0.75f -> {
                applicationScope.launch {
                    podcastsRepository.updateCurrentlyPlayingEpisodeSpeed(1.0f)
                }
                controller?.setPlaybackSpeed(1.0f)
                1.0f
            }
            else -> 1.0f
        }
    }

    fun replay(seconds: Int) {
        if (controller?.isPlaying == false) {
            _trackProgress.update { (it - seconds).coerceAtLeast(0) }
            return
        }
        _trackProgress.update { currPosition ->
            val newPosition = (currPosition - seconds).coerceAtLeast(0)
            controller?.seekTo(newPosition.toLong() * 1000)
            newPosition
        }
    }

    fun forward(seconds: Int) {
        if (controller?.isPlaying == false) {
            _trackProgress.update { (it + seconds).coerceAtMost(currentlyPlayingEpisode.value?.episode?.durationInSec ?: Int.MAX_VALUE) }
            return
        }
        _trackProgress.update { currPosition ->
            val newPosition = (currPosition + seconds).coerceAtMost(controller?.duration?.toInt() ?: Int.MAX_VALUE)
            controller?.seekTo(newPosition.toLong() * 1000)
            newPosition
        }
    }

    fun seekTo(seconds: Int) {
        val newPosition = seconds * 1000L
        _trackProgress.update { seconds }
        controller?.seekTo(newPosition)
    }

    fun releasePlayer() {
        MediaController.releaseFuture(controllerFuture)
    }

    fun consumeErrorPlayingStatus() {
        podcastsRepository.updateCurrentlyPlayingEpisodeStatus(PlayingStatus.Paused)
    }

    private fun seekToAndPlay(positionInSec: Int?) {
        if (positionInSec != null && positionInSec != 0) {
            controller?.seekTo(positionInSec * 1000L)
        }
        controller?.play()
    }
}
