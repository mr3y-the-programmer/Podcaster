package com.mr3y.podcaster.core.data.internal

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapBoth
import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.core.local.dao.PodcastsDao
import com.mr3y.podcaster.core.local.dao.RecentSearchesDao
import com.mr3y.podcaster.core.model.CurrentlyPlayingEpisode
import com.mr3y.podcaster.core.model.Episode
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

    override fun getEpisodesForPodcasts(podcastsIds: Set<Long>, limit: Long) = podcastsDao.getEpisodesForPodcasts(podcastsIds, limit)

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

    override suspend fun getEpisode(episodeId: Long, podcastArtworkUrl: String, forceRefresh: Boolean): Episode? {
        suspend fun fetchFromNetwork(): Episode? {
            return networkClient.getEpisodeById(episodeId).mapBoth(
                success = { it.mapToEpisode(null, podcastArtworkUrl) },
                failure = { null }
            )
        }

        return if (forceRefresh) {
            fetchFromNetwork()
        } else {
            val localEpisode = podcastsDao.getEpisode(episodeId)
            localEpisode ?: fetchFromNetwork()
        }
    }

    override fun getCurrentlyPlayingEpisode(): Flow<CurrentlyPlayingEpisode?> = podcastsDao.getCurrentlyPlayingEpisode()

    override fun getDownloadedEpisodes(): Flow<List<Episode>> = podcastsDao.getDownloadedEpisodes()

    override fun downloadEpisode(episodeId: Long) {
        TODO("Not yet implemented")
    }

    override fun cancelDownloadingEpisode(episodeId: Long) {
        TODO("Not yet implemented")
    }

    override fun setCurrentlyPlayingEpisode(episode: CurrentlyPlayingEpisode) {
        podcastsDao.setCurrentlyPlayingEpisode(episode)
    }

    override fun updateCurrentlyPlayingEpisodeStatus(newStatus: PlayingStatus) {
        podcastsDao.updateCurrentlyPlayingEpisodeStatus(newStatus)
    }

    override fun updateEpisodePlaybackProgress(progressInSec: Int?, episodeId: Long) {
        podcastsDao.updateEpisodePlaybackProgress(progressInSec, episodeId)
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
