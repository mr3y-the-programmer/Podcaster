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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
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
                    val (episode, playingStatus) = it
                    val uri = Uri.Builder()
                        .encodedPath(episode.enclosureUrl)
                        .build()
                    val mediaItem = MediaItem.fromUri(uri)
                    val internetDataSource =
                        ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
                            .createMediaSource(mediaItem)
                    setMediaSource(internetDataSource)
                    if (playingStatus == PlayingStatus.Playing || playingStatus == PlayingStatus.Loading) {
                        prepare()
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

    init {
        applicationScope.launch {
            while (true) {
                val progressInSec = exoPlayer?.currentPosition?.div(1000)
                if (progressInSec != null && progressInSec != 0L) {
                    currentlyPlayingEpisode.value?.let { (episode, _) ->
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
        currentlyPlayingEpisode.value?.let { (currentEpisode, _) ->
            if (currentEpisode.id == episode.id) {
                resume()
                return
            }
        }
        podcastsRepository.setCurrentlyPlayingEpisode(CurrentlyPlayingEpisode(episode, PlayingStatus.Loading))
    }

    fun resume() {
        currentlyPlayingEpisode.value?.let {
            seekToAndPlay(it.episode.progressInSec)
            podcastsRepository.updateCurrentlyPlayingEpisodeStatus(PlayingStatus.Playing)
        }
    }

    fun pause() {
        exoPlayer?.pause()
        podcastsRepository.updateCurrentlyPlayingEpisodeStatus(PlayingStatus.Paused)
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
