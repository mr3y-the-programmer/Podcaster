package com.mr3y.podcaster.ui.presenter

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.core.model.CurrentlyPlayingEpisode
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.PlayingStatus
import com.mr3y.podcaster.service.DownloadMediaService
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
    @ApplicationScope private val applicationScope: CoroutineScope,
) {

    private var controller: MediaController? = null
    private var currentContext: Context? = null
    private lateinit var controllerFuture: ListenableFuture<MediaController>

    val currentlyPlayingEpisode = podcastsRepository.getCurrentlyPlayingEpisode()
        .onEach {
            if (it != null) {
                _trackProgress.update { _ -> it.episode.progressInSec ?: 0 }
            }
        }
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )

    private val _isPlayerViewExpanded = MutableStateFlow(false)
    val isPlayerViewExpanded = _isPlayerViewExpanded.asStateFlow()

    private val _trackProgress = MutableStateFlow(0)
    val trackProgress = _trackProgress.asStateFlow()

    val canSeekToNextInQueue: Boolean
        get() = controller?.hasNextMediaItem() ?: false

    val canSeekToPreviousInQueue: Boolean
        get() = controller?.hasPreviousMediaItem() ?: false

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
                controller?.apply {
                    currentlyPlayingEpisode.value?.let { (episode, episodePlayingStatus, playingSpeed) ->
                        setMediaItemForEpisode(episode)
                        setPlaybackSpeed(playingSpeed)
                        maybeAddQueueEpisodes()

                        if ((episodePlayingStatus == PlayingStatus.Playing || episodePlayingStatus == PlayingStatus.Loading) && !isPlaying) {
                            seekToAndPlay(episode.progressInSec)
                            if (episodePlayingStatus == PlayingStatus.Loading) {
                                podcastsRepository.updateCurrentlyPlayingEpisodeStatus(PlayingStatus.Playing)
                            }
                        }
                    }
                }
                context.startService(Intent(context, PlaybackService::class.java))
            },
            MoreExecutors.directExecutor(),
        )
        currentContext = context
    }

    fun play(episode: Episode) {
        currentlyPlayingEpisode.value?.let { (currentEpisode, _, _) ->
            if (currentEpisode.id == episode.id) {
                resume()
                return
            }
        }
        val currentEpisodeId = currentlyPlayingEpisode.value?.episode?.id
        val playbackSpeed = currentlyPlayingEpisode.value?.playingSpeed ?: 1.0f
        _trackProgress.update { episode.progressInSec ?: 0 }
        podcastsRepository.setCurrentlyPlayingEpisode(CurrentlyPlayingEpisode(episode, PlayingStatus.Loading, playbackSpeed))
        if (currentEpisodeId != null) {
            podcastsRepository.replaceEpisodeInQueue(episode, currentEpisodeId)
        } else {
            podcastsRepository.addEpisodeToQueue(episode)
        }
        controller?.setMediaItemForEpisode(episode)
        controller?.setPlaybackSpeed(playbackSpeed)
        seekToAndPlay(_trackProgress.value)
        podcastsRepository.updateCurrentlyPlayingEpisodeStatus(PlayingStatus.Playing)
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
        return when (currentSpeed) {
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
            controller?.seekTo(_trackProgress.value.toLong().coerceAtLeast(0) * 1000L)
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
            controller?.seekTo(_trackProgress.value.toLong().coerceAtMost(controller?.duration ?: Long.MAX_VALUE) * 1000L)
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

    fun downloadEpisode(episode: Episode) {
        val context = currentContext
        if (context != null) {
            podcastsRepository.addEpisodeOnDeviceIfNotExist(episode)
            val downloadRequest = DownloadRequest.Builder(
                episode.id.toString(),
                Uri.Builder()
                    .encodedPath(episode.enclosureUrl)
                    .build(),
            ).build()
            DownloadService.sendAddDownload(
                context.applicationContext,
                DownloadMediaService::class.java,
                downloadRequest,
                false,
            )
        }
    }

    fun resumeDownloading(episodeId: Long) {
        val context = currentContext
        if (context != null) {
            DownloadService.sendSetStopReason(context.applicationContext, DownloadMediaService::class.java, episodeId.toString(), DownloadMediaService.DownloadResumed, false)
        }
    }

    fun pauseDownloading(episodeId: Long) {
        val context = currentContext
        if (context != null) {
            DownloadService.sendSetStopReason(context.applicationContext, DownloadMediaService::class.java, episodeId.toString(), DownloadMediaService.DownloadPaused, false)
        }
    }

    fun addToQueue(episode: Episode) {
        currentlyPlayingEpisode.value?.let { (currentEpisode, _, _) ->
            if (currentEpisode.id == episode.id || isEpisodeInQueue(episode.id)) {
                return
            }
        }
        podcastsRepository.addEpisodeToQueue(episode)
        controller?.addMediaItemForEpisode(episode)
    }

    fun removeFromQueue(episodeId: Long) {
        currentlyPlayingEpisode.value?.let { (currentEpisode, _, _) ->
            if (currentEpisode.id == episodeId) {
                return
            }
        }
        val queueSize = controller?.mediaItemCount ?: return
        for (i in 0..<queueSize) {
            val mediaItemEpisodeId = controller?.getMediaItemAt(i)?.mediaId?.toLong() ?: break
            if (mediaItemEpisodeId == episodeId) {
                controller?.removeMediaItem(i)
                podcastsRepository.removeEpisodeFromQueue(episodeId)
                break
            }
        }
    }

    fun seekToNextInQueue() {
        if (canSeekToNextInQueue) {
            controller?.seekToNextMediaItem()
        }
    }

    fun seekToPreviousInQueue() {
        if (canSeekToPreviousInQueue) {
            controller?.seekToPreviousMediaItem()
        }
    }

    fun isEpisodeInQueue(episodeId: Long): Boolean {
        return podcastsRepository.isEpisodeInQueue(episodeId)
    }

    fun releasePlayer() {
        MediaController.releaseFuture(controllerFuture)
        currentContext = null
    }

    fun consumeErrorPlayingStatus() {
        podcastsRepository.updateCurrentlyPlayingEpisodeStatus(PlayingStatus.Paused)
    }

    internal fun Player.setMediaItemForEpisode(episode: Episode) {
        val mediaItem = MediaItem.fromUri(episode.enclosureUrl.toUri())
            .buildUpon()
            .setMediaId(episode.id.toString())
            .setMediaMetadata(episode.mediaMetadata())
            .build()

        if (!hasPreviousMediaItem() && !hasNextMediaItem()) {
            setMediaItem(mediaItem, episode.progressInSec?.times(1000)?.toLong() ?: 0L)
        } else {
            replaceMediaItem(currentMediaItemIndex, mediaItem)
            seekTo(episode.progressInSec?.times(1000)?.toLong() ?: 0L)
        }
    }

    private fun Player.addMediaItemForEpisode(episode: Episode) {
        val mediaItem = MediaItem.fromUri(episode.enclosureUrl.toUri())
            .buildUpon()
            .setMediaId(episode.id.toString())
            .setMediaMetadata(episode.mediaMetadata())
            .build()
        addMediaItem(mediaItem)
    }

    private fun seekToAndPlay(positionInSec: Int?) {
        controller?.prepare()
        if (positionInSec != null && positionInSec != 0) {
            controller?.seekTo(positionInSec * 1000L)
        }
        controller?.play()
    }

    internal fun Player.maybeAddQueueEpisodes() {
        // add all episodes in the app's local queue to player playlist and maintain their order.
        val episodesQueue = podcastsRepository.getQueueEpisodes()

        if (episodesQueue.isEmpty() || mediaItemCount == 0) return

        // prepend all the episodes that were before the current episode in the playlist.
        var nextQueueEpisodeIndex = 0
        if (mediaItemCount != episodesQueue.size) {
            currentlyPlayingEpisode.value?.episode?.let { currentEpisode ->
                while (episodesQueue[nextQueueEpisodeIndex].id != currentEpisode.id) {
                    addMediaItemForEpisode(episodesQueue[nextQueueEpisodeIndex])
                    moveMediaItem(currentMediaItemIndex, currentMediaItemIndex + 1)
                    nextQueueEpisodeIndex++
                }
            }
        }

        // append all the episodes that were after the current episode in the playlist.
        if (mediaItemCount < episodesQueue.size) {
            episodesQueue.drop(nextQueueEpisodeIndex + 1).forEach { episode ->
                addMediaItemForEpisode(episode)
            }
        }
    }

    private fun Episode.mediaMetadata(): MediaMetadata {
        return MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(podcastTitle)
            .setIsBrowsable(false)
            .setIsPlayable(true)
            .setArtworkUri(Uri.parse(artworkUrl))
            .setMediaType(MediaMetadata.MEDIA_TYPE_PODCAST_EPISODE)
            .build()
    }

    private fun String.toUri(): Uri {
        return Uri.Builder()
            .encodedPath(this)
            .build()
    }
}
