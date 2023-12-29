package com.mr3y.podcaster.service

import android.content.Intent
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ConnectionResult
import androidx.media3.session.MediaSession.ConnectionResult.AcceptedResultBuilder
import androidx.media3.session.MediaSession.MediaItemsWithStartPosition
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.ListenableFuture
import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.core.model.CurrentlyPlayingEpisode
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
        val player = ExoPlayer.Builder(this)
//            .setAudioAttributes()
            .setPauseAtEndOfMediaItems(true)
            .setMediaSourceFactory(ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory()))
            .build()
        mediaSession = MediaSession.Builder(this, player)
            .setCallback(PlaybackMediaSessionCallback())
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startListeningForUpdates()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startListeningForUpdates() {
        serviceScope.launch {
            podcastsRepository.getCurrentlyPlayingEpisode().collectLatest { currentEpisode ->
                currentlyPlayingEpisode.update { currentEpisode }
                val player = mediaSession?.player
                while (true) {
                    val episodeId = player?.currentMediaItem?.mediaId?.toLongOrNull() ?: break
                    val progressInSec = player.currentPosition.div(1000)
                    if (progressInSec != 0L && player.isPlaying) {
                        podcastsRepository.updateEpisodePlaybackProgress(progressInSec.toInt(), episodeId)
                    }
                    delay(1.seconds)
                }
            }
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
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            return AcceptedResultBuilder(session)
                .setAvailableSessionCommands(ConnectionResult.DEFAULT_SESSION_COMMANDS)
                .setAvailablePlayerCommands(ConnectionResult.DEFAULT_PLAYER_COMMANDS)
                .build()
        }

        override fun onPlaybackResumption(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo
        ): ListenableFuture<MediaItemsWithStartPosition> {
            return serviceScope.future {
                val currentEpisode = currentlyPlayingEpisode.value
                if (currentEpisode != null) {
                    val episode = currentEpisode.episode
                    val startingPosition = episode.progressInSec?.times(1000)?.toLong() ?: C.TIME_UNSET
                    val mediaMetadata = MediaMetadata.Builder()
                        .setTitle(episode.title)
                        .setArtist(episode.podcastTitle)
                        .setIsBrowsable(false)
                        .setIsPlayable(true)
                        .setArtworkUri(Uri.parse(episode.artworkUrl))
                        .setMediaType(MediaMetadata.MEDIA_TYPE_PODCAST_EPISODE)
                        .build()

                    MediaItemsWithStartPosition(
                        listOf(
                            MediaItem.Builder()
                                .setMediaId(episode.id.toString())
                                .setMediaMetadata(mediaMetadata)
                                .setUri(Uri.Builder().encodedPath(episode.enclosureUrl).build())
                                .build()
                        ),
                        C.INDEX_UNSET,
                        startingPosition
                    )
                } else {
                    throw UnsupportedOperationException()
                }
            }
        }
    }
}
