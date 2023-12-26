package com.mr3y.podcaster.ui.presenter

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.core.model.CurrentlyPlayingEpisode
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.PlayingStatus
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

    private var exoPlayer: ExoPlayer? = null

    val currentlyPlayingEpisode = podcastsRepository.getCurrentlyPlayingEpisode()
        .onEach {
            if (it != null) {
                exoPlayer?.apply {
                    val (episode, playingStatus, playingSpeed) = it
                    val uri = Uri.Builder()
                        .encodedPath(episode.enclosureUrl)
                        .build()
                    val mediaItem = MediaItem.fromUri(uri)
                    val internetDataSource =
                        ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
                            .createMediaSource(mediaItem)
                    setMediaSource(internetDataSource)
                    _trackProgress.update { episode.progressInSec ?: 0 }
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
                val progressInSec = exoPlayer?.currentPosition?.div(1000)
                if (progressInSec != null && progressInSec != 0L) {
                    currentlyPlayingEpisode.value?.let { (episode, _, _) ->
                        _trackProgress.update { progressInSec.toInt() }
                        podcastsRepository.updateEpisodePlaybackProgress(progressInSec.toInt(), episode.id)
                    }
                }
                delay(1.seconds)
            }
        }
    }

    fun initializePlayer(context: Context) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context)
                .build()
                .apply {
                    addListener(
                        object : Player.Listener {
                            override fun onPlayerError(error: PlaybackException) {
                                // TODO: log the error for better investigation
                                podcastsRepository.updateCurrentlyPlayingEpisodeStatus(PlayingStatus.Error)
                            }
                        }
                    )
                }
        }
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
        exoPlayer?.pause()
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
                exoPlayer?.setPlaybackSpeed(1.5f)
                1.5f

            }
            1.5f -> {
                applicationScope.launch {
                    podcastsRepository.updateCurrentlyPlayingEpisodeSpeed(2.0f)
                }
                exoPlayer?.setPlaybackSpeed(2.0f)
                2.0f

            }
            2.0f -> {
                applicationScope.launch {
                    podcastsRepository.updateCurrentlyPlayingEpisodeSpeed(0.75f)
                }
                exoPlayer?.setPlaybackSpeed(0.75f)
                0.75f
            }
            0.75f -> {
                applicationScope.launch {
                    podcastsRepository.updateCurrentlyPlayingEpisodeSpeed(1.0f)
                }
                exoPlayer?.setPlaybackSpeed(1.0f)
                1.0f
            }
            else -> 1.0f
        }
    }

    fun replay(seconds: Int) {
        if (exoPlayer?.isPlaying == false) {
            _trackProgress.update { (it - seconds).coerceAtLeast(0) }
            return
        }
        exoPlayer?.currentPosition?.let { currPosition ->
            val newPosition = (currPosition - (seconds * 1000)).coerceAtLeast(0)
            _trackProgress.update { (newPosition / 1000).toInt() }
            exoPlayer?.seekTo(newPosition)
        }
    }

    fun forward(seconds: Int) {
        if (exoPlayer?.isPlaying == false) {
            _trackProgress.update { (it + seconds).coerceAtMost(currentlyPlayingEpisode.value?.episode?.durationInSec ?: Int.MAX_VALUE) }
            return
        }
        exoPlayer?.currentPosition?.let { currPosition ->
            val newPosition = (currPosition + (seconds * 1000)).coerceAtMost(exoPlayer?.duration ?: Long.MAX_VALUE)
            _trackProgress.update { (newPosition / 1000).toInt() }
            exoPlayer?.seekTo(newPosition)
        }
    }

    fun seekTo(seconds: Int) {
        val newPosition = seconds * 1000L
        _trackProgress.update { seconds }
        exoPlayer?.seekTo(newPosition)
    }

    fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    fun consumeErrorPlayingStatus() {
        podcastsRepository.updateCurrentlyPlayingEpisodeStatus(PlayingStatus.Paused)
    }

    private fun seekToAndPlay(positionInSec: Int?) {
        if (positionInSec != null && positionInSec != 0) {
            exoPlayer?.seekTo(positionInSec * 1000L)
        }
        exoPlayer?.play()
    }
}
