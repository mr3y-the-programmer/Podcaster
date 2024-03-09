package com.mr3y.podcaster.core.opml

import com.github.michaelbull.result.mapBoth
import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.core.logger.Logger
import com.mr3y.podcaster.core.model.Podcast
import com.mr3y.podcaster.core.opml.di.IODispatcher
import com.mr3y.podcaster.core.opml.model.OpmlPodcast
import com.mr3y.podcaster.core.opml.model.OpmlResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpmlManager @Inject constructor(
    private val opmlAdapter: OpmlAdapter,
    private val fileManager: FileManager,
    private val repository: PodcastsRepository,
    @IODispatcher private val coroutineDispatcher: CoroutineDispatcher,
    private val logger: Logger
) {

    private val job = SupervisorJob() + coroutineDispatcher

    private val _result = MutableStateFlow<OpmlResult>(OpmlResult.Idle)
    val result: StateFlow<OpmlResult> = _result

    suspend fun import() {
        try {
            withContext(job) {
                val opmlXmlContent = fileManager.read()

                if (!opmlXmlContent.isNullOrBlank()) {
                    _result.emit(OpmlResult.Loading)
                    opmlAdapter.decode(opmlXmlContent)
                        .mapBoth(
                            success = {
                                addOpmlPodcasts(it)
                            },
                            failure = {
                                _result.emit(OpmlResult.Error.DecodingError)
                            }
                        )
                } else {
                    _result.emit(OpmlResult.Error.NoContentInOpmlFile)
                }
            }
        } catch (ex: Exception) {
            if (ex !is CancellationException) {
                logger.e(ex, tag = "OpmlManager") {
                    "Exception occurred on importing subscriptions collection."
                }
                _result.emit(OpmlResult.Error.UnknownFailure(ex))
            }
        }
    }

    suspend fun export() {
        try {
            withContext(job) {
                _result.emit(OpmlResult.Loading)

                repository.getSubscriptionsNonObservable()
                    .let { opmlAdapter.encode(it) }
                    .mapBoth(
                        success = {
                            fileManager.save(OpmlFileName, it)
                        },
                        failure = {
                            _result.emit(OpmlResult.Error.EncodingError)
                        }
                    )
            }
        } catch (ex: Exception) {
            if (ex !is CancellationException) {
                logger.e(ex, tag = "OpmlManager") {
                    "Exception occurred on exporting subscriptions collection."
                }
                _result.emit(OpmlResult.Error.UnknownFailure(ex))
            }
        }
    }

    fun cancelCurrentRunningTask() {
        job.cancelChildren()
        _result.tryEmit(OpmlResult.Idle)
    }

    suspend fun resetResultState() {
        _result.emit(OpmlResult.Idle)
    }

    private suspend fun addOpmlPodcasts(opmlPodcasts: List<OpmlPodcast>) = coroutineScope {
        if (opmlPodcasts.size > PageSize) {
            opmlPodcasts.reversed().chunked(PageSize).forEach { podcastsGroup ->
                podcastsGroup
                    .map { opmlPodcast ->
                        launch {
                            repository.getPodcast(podcastFeedUrl = opmlPodcast.link).mapBoth(
                                success = { podcast ->
                                    addPodcastToSubscriptionsIfNotExist(podcast)
                                },
                                failure = {
                                    _result.emit(OpmlResult.Error.NetworkError)
                                }
                            )
                        }
                    }
                    .joinAll()
            }
        } else {
            opmlPodcasts.reversed().forEach { opmlPodcast ->
                repository.getPodcast(podcastFeedUrl = opmlPodcast.link).mapBoth(
                    success = { podcast ->
                        addPodcastToSubscriptionsIfNotExist(podcast)
                    },
                    failure = {
                        _result.emit(OpmlResult.Error.NetworkError)
                    }
                )
            }
        }
    }

    private suspend fun addPodcastToSubscriptionsIfNotExist(podcast: Podcast) {
        if (!repository.isPodcastFromSubscriptionsNonObservable(podcast.id)) {
            val episodes = repository.getEpisodesForPodcast(podcast.id, podcast.title, podcast.artworkUrl, true)
            if (episodes != null) {
                repository.subscribeToPodcast(podcast, episodes)
            } else {
                _result.emit(OpmlResult.Error.NetworkError)
            }
        }
    }

    companion object {
        private const val PageSize = 10
        private const val OpmlFileName = "podcaster_subscriptions.xml"
    }
}
