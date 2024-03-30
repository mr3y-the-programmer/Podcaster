package com.mr3y.podcaster.service

import android.content.Intent
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
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
import com.mr3y.podcaster.core.model.Episode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.guava.future
import javax.inject.Inject

@OptIn(UnstableApi::class)
@AndroidEntryPoint
class PlaybackService : MediaSessionService() {

    @Inject
    lateinit var podcastsRepository: PodcastsRepository

    private val serviceScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var mediaSession: MediaSession? = null
    private lateinit var mediaPlayer: ServiceMediaPlayer

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
        mediaPlayer = ServiceMediaPlayer(mediaSession!!.player, podcastsRepository)
        mediaPlayer.attachPlayerListener()
        mediaPlayer.startListeningForUpdatesIn(serviceScope)
        return super.onStartCommand(intent, flags, startId)
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
