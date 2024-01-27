package com.mr3y.podcaster.service

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.Scheduler
import androidx.media3.exoplayer.workmanager.WorkManagerScheduler
import com.mr3y.podcaster.R
import com.mr3y.podcaster.Strings
import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.core.model.EpisodeDownloadStatus
import com.mr3y.podcaster.ui.resources.EnStrings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import java.io.File
import java.lang.Exception
import javax.inject.Inject

@OptIn(UnstableApi::class)
@AndroidEntryPoint
@ExperimentalCoroutinesApi
class DownloadMediaService : DownloadService(
    DOWNLOAD_NOTIFICATION_ID,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    DOWNLOAD_NOTIFICATION_CHANNEL_ID,
    androidx.media3.exoplayer.workmanager.R.string.exo_download_notification_channel_name,
    R.string.downloads_notification_channel_description
) {

    @Inject
    lateinit var podcastsRepository: PodcastsRepository

    private var downloadManager: DownloadManager? = null

    override fun getDownloadManager(): DownloadManager = buildDownloadManager(this)

    override fun getScheduler(): Scheduler = WorkManagerScheduler(this, DOWNLOAD_WORK_NAME)

    override fun getForegroundNotification(
        downloads: MutableList<Download>,
        notMetRequirements: Int
    ): Notification {
        val languageCode = Resources.getSystem().configuration.locales[0].language.lowercase()
        val strings = Strings[languageCode] ?: EnStrings
        // Update our app UI.
        downloads.forEach { download ->
            val episodeId = download.request.id.toLong()
            podcastsRepository.updateEpisodeDownloadProgress(episodeId, download.percentDownloaded.div(100.0f).coerceIn(0f, 1f))
        }
        // Update foreground service notification.
        return DownloadNotificationHelper(this, DOWNLOAD_NOTIFICATION_CHANNEL_ID)
            .buildProgressNotification(
                this,
                R.drawable.core_common_ic_nia_notification,
                null,
                strings.download_work_notification_message,
                downloads,
                notMetRequirements
            )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // start listening to updates because at this point download
        // manager is guaranteed to have been initialized & podcasts repository
        // instance is guaranteed to have been injected.
        attachDownloadManagerListener()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun buildDownloadManager(context: Context): DownloadManager {
        if (downloadManager == null) {
            val databaseProvider = buildDatabaseProvider(context)
            val cache = buildSimpleCache(context)
            val downloadExecutor = Dispatchers.IO.limitedParallelism(4).asExecutor()
            downloadManager = DownloadManager(context, databaseProvider, cache, upStreamFactory, downloadExecutor).apply {
                maxParallelDownloads = 1
            }
        }
        return downloadManager!!
    }

    private fun attachDownloadManagerListener() {
        downloadManager?.apply {
            addListener(
                object : DownloadManager.Listener {

                    override fun onDownloadRemoved(
                        downloadManager: DownloadManager,
                        download: Download
                    ) {
                        val episodeId = download.request.id.toLong()
                        podcastsRepository.updateEpisodeDownloadStatus(episodeId, EpisodeDownloadStatus.NotDownloaded)
                        podcastsRepository.updateEpisodeDownloadProgress(episodeId, 0f)
                    }

                    override fun onDownloadChanged(
                        downloadManager: DownloadManager,
                        download: Download,
                        finalException: Exception?
                    ) {
                        val episodeId = download.request.id.toLong()
                        when (download.state) {
                            Download.STATE_QUEUED -> {
                                podcastsRepository.updateEpisodeDownloadStatus(episodeId, EpisodeDownloadStatus.Queued)
                            }
                            Download.STATE_DOWNLOADING -> {
                                podcastsRepository.updateEpisodeDownloadStatus(episodeId, EpisodeDownloadStatus.Downloading)
                            }
                            Download.STATE_COMPLETED -> {
                                podcastsRepository.updateEpisodeDownloadStatus(episodeId, EpisodeDownloadStatus.Downloaded)
                            }
                            Download.STATE_STOPPED -> {
                                podcastsRepository.updateEpisodeDownloadStatus(episodeId, EpisodeDownloadStatus.Paused)
                            }
                            Download.STATE_FAILED -> {
                                // TODO: log the error for better investigation.
                                podcastsRepository.updateEpisodeDownloadStatus(episodeId, EpisodeDownloadStatus.NotDownloaded)
                            }
                            else -> {}
                        }
                    }
                }
            )
        }
    }

    companion object {
        const val DownloadResumed = Download.STOP_REASON_NONE
        const val DownloadPaused = 35
        private const val DOWNLOAD_NOTIFICATION_ID = 20
        private const val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "DownloadNotificationChannel"
        private const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"
        private const val DOWNLOAD_WORK_NAME = "download_episode_request"

        private val upStreamFactory = DefaultHttpDataSource.Factory()
        private var cache: Cache? = null
        private var databaseProvider: DatabaseProvider? = null
        private var downloadDirectory: File? = null

        fun buildCacheDataSourceFactory(context: Context): DataSource.Factory {
            return CacheDataSource.Factory()
                .setCache(buildSimpleCache(context))
                .setUpstreamDataSourceFactory(upStreamFactory)
                .setCacheWriteDataSinkFactory(null)
        }

        private fun buildSimpleCache(context: Context): Cache {
            if (cache == null) {
                cache = SimpleCache(getDownloadDirectory(context), NoOpCacheEvictor(), buildDatabaseProvider(context))
            }
            return cache!!
        }

        private fun buildDatabaseProvider(context: Context): DatabaseProvider {
            if (databaseProvider == null) {
                databaseProvider = StandaloneDatabaseProvider(context)
            }
            return databaseProvider!!
        }

        private fun getDownloadDirectory(context: Context): File {
            if (downloadDirectory == null) {
                val appExternalDir = context.getExternalFilesDir(null) ?: context.filesDir
                downloadDirectory = File(appExternalDir, DOWNLOAD_CONTENT_DIRECTORY)
            }
            return downloadDirectory!!
        }
    }
}
