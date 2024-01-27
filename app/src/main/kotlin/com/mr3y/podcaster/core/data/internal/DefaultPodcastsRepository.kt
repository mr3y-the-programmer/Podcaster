package com.mr3y.podcaster.core.data.internal

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapBoth
import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.core.local.dao.PodcastsDao
import com.mr3y.podcaster.core.local.dao.RecentSearchesDao
import com.mr3y.podcaster.core.model.CurrentlyPlayingEpisode
import com.mr3y.podcaster.core.model.EpisodeDownloadMetadata
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.EpisodeDownloadStatus
import com.mr3y.podcaster.core.model.PlayingStatus
import com.mr3y.podcaster.core.model.Podcast
import com.mr3y.podcaster.core.network.PodcastIndexClient
import com.mr3y.podcaster.core.network.utils.mapToEpisode
import com.mr3y.podcaster.core.network.utils.mapToEpisodes
import com.mr3y.podcaster.core.network.utils.mapToPodcast
import com.mr3y.podcaster.core.network.utils.mapToPodcasts
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultPodcastsRepository @Inject constructor(
    private val podcastsDao: PodcastsDao,
    private val recentSearchesDao: RecentSearchesDao,
    private val networkClient: PodcastIndexClient
) : PodcastsRepository {

    override fun getSubscriptions() = podcastsDao.getAllPodcasts()

    override suspend fun getSubscriptionsNonObservable(): List<Podcast> = podcastsDao.getAllPodcastsNonObservable()

    override fun getEpisodesForPodcasts(podcastsIds: Set<Long>, limit: Long) = podcastsDao.getEpisodesForPodcasts(podcastsIds, limit)

    override fun getEpisodesWithDownloadMetadataForPodcasts(podcastsIds: Set<Long>, limit: Long) = podcastsDao.getEpisodeWithDownloadMetadataForPodcasts(podcastsIds, limit)

    override fun getDownloads() = podcastsDao.getDownloadingEpisodesWithDownloadMetadata()

    override suspend fun getPodcast(podcastId: Long, forceRefresh: Boolean): Podcast? {
        suspend fun fetchFromNetwork(): Podcast? {
            return networkClient.getPodcastById(podcastId).mapBoth(
                success = { it.mapToPodcast() },
                failure = { null }
            )
        }

        return if (forceRefresh) {
            fetchFromNetwork()
        } else {
            val localPodcast = podcastsDao.getPodcast(podcastId)
            localPodcast ?: fetchFromNetwork()
        }
    }

    override suspend fun getEpisodesForPodcast(podcastId: Long, podcastTitle: String, podcastArtworkUrl: String, forceRefresh: Boolean): List<Episode>? {
        val isPodcastAvailableLocally = podcastsDao.isPodcastAvailableNonObservable(podcastId)
        return if (forceRefresh || !isPodcastAvailableLocally) {
            networkClient.getEpisodesByPodcastId(podcastId)
                .mapBoth(
                    success = { it.mapToEpisodes(podcastTitle, podcastArtworkUrl) },
                    failure = { null }
                )
        } else {
            podcastsDao.getEpisodesForPodcast(podcastId)
        }
    }

    override fun isPodcastFromSubscriptions(podcastId: Long): Flow<Boolean> {
        return podcastsDao.isPodcastAvailable(podcastId)
    }

    override fun hasSubscriptions(): Flow<Boolean> = podcastsDao.hasPodcasts()

    override fun hasDownloads(): Flow<Boolean> = podcastsDao.hasDownloads()

    override suspend fun getEpisode(episodeId: Long, podcastArtworkUrl: String, forceRefresh: Boolean): Episode? {
        suspend fun fetchFromNetworkAndRefresh(): Episode? {
            return networkClient.getEpisodeById(episodeId).mapBoth(
                success = { networkEpisode ->
                    networkEpisode.mapToEpisode(null, podcastArtworkUrl).also {
                        podcastsDao.upsertEpisode(it)
                    }
                },
                failure = { null }
            )
        }

        return if (forceRefresh) {
            fetchFromNetworkAndRefresh()
        } else {
            val localEpisode = podcastsDao.getEpisode(episodeId)
            localEpisode ?: fetchFromNetworkAndRefresh()
        }
    }

    override fun getCurrentlyPlayingEpisode(): Flow<CurrentlyPlayingEpisode?> = podcastsDao.getCurrentlyPlayingEpisode()

    override fun setCurrentlyPlayingEpisode(episode: CurrentlyPlayingEpisode) {
        podcastsDao.setCurrentlyPlayingEpisode(episode)
    }

    override fun updateCurrentlyPlayingEpisodeStatus(newStatus: PlayingStatus) {
        podcastsDao.updateCurrentlyPlayingEpisodeStatus(newStatus)
    }

    override suspend fun updateCurrentlyPlayingEpisodeSpeed(newSpeed: Float) {
        podcastsDao.updateCurrentlyPlayingEpisodeSpeed(newSpeed)
    }

    override fun updateEpisodePlaybackProgress(progressInSec: Int?, episodeId: Long) {
        podcastsDao.updateEpisodePlaybackProgress(progressInSec, episodeId)
    }

    override fun updateEpisodeDownloadStatus(episodeId: Long, newStatus: EpisodeDownloadStatus) {
        podcastsDao.updateEpisodeDownloadStatus(episodeId, newStatus)
    }

    override fun updateEpisodeDownloadProgress(episodeId: Long, progress: Float) {
        podcastsDao.updateEpisodeDownloadProgress(episodeId, progress)
    }

    override fun getEpisodeDownloadMetadata(episodeId: Long): Flow<EpisodeDownloadMetadata?> = podcastsDao.getEpisodeDownloadMetadataById(episodeId)

    override fun addEpisodeOnDeviceIfNotExist(episode: Episode) {
        if (!podcastsDao.isEpisodeAvailableNonObservable(episode.id)) {
            podcastsDao.addEpisode(episode)
        }
    }

    override fun markEpisodeAsCompleted(episodeId: Long) {
        podcastsDao.markEpisodeAsCompleted(episodeId)
    }

    override fun subscribeToPodcast(podcast: Podcast, episodes: List<Episode>) {
        podcastsDao.upsertPodcast(podcast)
        episodes.forEach { episode ->
            podcastsDao.upsertEpisode(episode)
        }
    }

    override fun unSubscribeFromPodcast(podcastId: Long) {
        podcastsDao.deletePodcast(podcastId)
        podcastsDao.deleteUntouchedEpisodes(podcastId)
    }

    override suspend fun searchForPodcastsByTerm(text: String): Result<List<Podcast>, Any> {
        return networkClient.searchForPodcastsByTerm(text).map { it.mapToPodcasts() }
    }

    override suspend fun getPodcast(podcastFeedUrl: String): Result<Podcast, Any> {
        return networkClient.getPodcastByFeedUrl(podcastFeedUrl).map { it.mapToPodcast() }
    }

    override fun getRecentSearchQueries(): Flow<List<String>> = recentSearchesDao.recentSearchQueries()

    override fun saveNewSearchQuery(searchQuery: String) = recentSearchesDao.addNewSearchQuery(searchQuery)

    override fun deleteSearchQuery(searchQuery: String) = recentSearchesDao.deleteSearchQuery(searchQuery)

    override suspend fun syncRemotePodcastWithLocal(podcastId: Long): Boolean {
        return networkClient.getPodcastById(podcastId)
            .map {
                val podcast = it.mapToPodcast()
                if (podcastsDao.isPodcastAvailableNonObservable(podcastId)) {
                    podcastsDao.upsertPodcast(podcast)
                    podcastsDao.updateEpisodesPodcastTitle(title = podcast.title, podcastId = podcast.id)
                }
            }.mapBoth(
                success = { true },
                failure = { false }
            )
    }

    override suspend fun syncRemoteEpisodesForPodcastWithLocal(podcastId: Long, podcastTitle: String, podcastArtworkUrl: String): Boolean {
        return networkClient.getEpisodesByPodcastId(podcastId)
            .map {
                val episodes = it.mapToEpisodes(podcastTitle, podcastArtworkUrl)
                if (podcastsDao.isPodcastAvailableNonObservable(podcastId)) {
                    episodes.forEach { episode -> podcastsDao.upsertEpisode(episode) }
                    return@map
                }
                episodes.forEach { episode ->
                    if (podcastsDao.isEpisodeAvailableNonObservable(episode.id)) {
                        podcastsDao.upsertEpisode(episode)
                    }
                }
            }.mapBoth(
                success = { true },
                failure = { false }
            )
    }

    override suspend fun syncRemoteEpisodeWithLocal(episodeId: Long, podcastArtworkUrl: String): Boolean {
        return networkClient.getEpisodeById(episodeId)
            .map {
                podcastsDao.upsertEpisode(it.mapToEpisode(null, podcastArtworkUrl))
            }.mapBoth(
                success = { true },
                failure = { false }
            )
    }
}
