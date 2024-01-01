package com.mr3y.podcaster.service

import android.app.Notification
import android.content.Context
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
import com.mr3y.podcaster.ui.resources.EnStrings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class, UnstableApi::class)
@AndroidEntryPoint
class DownloadMediaService : DownloadService(DOWNLOAD_NOTIFICATION_ID) {

    @Inject
    lateinit var podcastsRepository: PodcastsRepository
    private val serviceScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var downloadManager: DownloadManager? = null

    private val isDownloadManagerInitialized = MutableStateFlow(false)

    init {
        serviceScope.launch {
            isDownloadManagerInitialized.filter { it }.collectLatest {
                while (downloadManager!!.currentDownloads.isNotEmpty()) {

                    delay(1.seconds)
                }
            }
        }
    }

    override fun getDownloadManager(): DownloadManager = buildDownloadManager(this)

    override fun getScheduler(): Scheduler = WorkManagerScheduler(this, DOWNLOAD_WORK_NAME)

    override fun getForegroundNotification(
        downloads: MutableList<Download>,
        notMetRequirements: Int
    ): Notification {
        val languageCode = Resources.getSystem().configuration.locales[0].language.lowercase()
        val strings = Strings[languageCode] ?: EnStrings
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

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun buildDownloadManager(context: Context): DownloadManager {
        if (downloadManager == null) {
            val databaseProvider = buildDatabaseProvider(context)
            val cache = buildSimpleCache(context)
            val downloadExecutor = Dispatchers.IO.limitedParallelism(4).asExecutor()
            downloadManager = DownloadManager(context, databaseProvider, cache, upStreamFactory, downloadExecutor).apply {
                maxParallelDownloads = 3
                addListener(
                    object : DownloadManager.Listener {
                        override fun onDownloadsPausedChanged(
                            downloadManager: DownloadManager,
                            downloadsPaused: Boolean
                        ) {
                            super.onDownloadsPausedChanged(downloadManager, downloadsPaused)
                        }

                        override fun onDownloadChanged(
                            downloadManager: DownloadManager,
                            download: Download,
                            finalException: Exception?
                        ) {
                            if (download.isTerminalState && download.state == Download.STATE_COMPLETED) {
//                                podcastsRepository.
                            }
                        }
                    }
                )
                isDownloadManagerInitialized.update { true }
            }
        }
        return downloadManager!!
    }

    companion object {
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
