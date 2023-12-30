package com.mr3y.podcaster.core.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.mr3y.podcaster.core.data.PodcastsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.TimeUnit

@HiltWorker
class SubscriptionsSyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val podcastsRepository: PodcastsRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        setSafeForeground(getForegroundInfo())
        return coroutineScope {
            val aggregatedSyncResults = podcastsRepository.getSubscriptionsNonObservable().map { podcast ->
                    async {
                        val result1 = podcastsRepository.syncRemotePodcastWithLocal(podcast.id)
                        val result2 = podcastsRepository.syncRemoteEpisodesForPodcastWithLocal(
                            podcast.id,
                            podcast.title,
                            podcast.artworkUrl
                        )
                        result1 && result2
                    }
                }.awaitAll()

            when {
                aggregatedSyncResults.all { isSuccessful -> isSuccessful } -> Result.success()
                else -> {
                    // TODO: Log more info for better investigation.
                    Result.failure()
                }
            }
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return context.syncForegroundInfo()
    }

    private suspend fun setSafeForeground(foregroundInfo: ForegroundInfo) {
        try {
            setForeground(foregroundInfo)
        } catch (exception: IllegalStateException) {
            // TODO: Log more info for better investigation.
        }
    }

    companion object {
        const val PeriodicWorkRequestID = "subscriptionsPeriodicSyncWorker"

        fun subscriptionsPeriodicSyncWorker() = PeriodicWorkRequestBuilder<SubscriptionsSyncWorker>(8, TimeUnit.HOURS)
            .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
            .build()
    }
}
