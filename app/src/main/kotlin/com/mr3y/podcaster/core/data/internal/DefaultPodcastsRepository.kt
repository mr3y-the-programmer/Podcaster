package com.mr3y.podcaster.core.data.internal

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapBoth
import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.core.local.dao.PodcastsDao
import com.mr3y.podcaster.core.local.dao.RecentSearchesDao
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.Podcast
import com.mr3y.podcaster.core.network.PodcastIndexClient
import com.mr3y.podcaster.core.network.utils.mapToEpisode
import com.mr3y.podcaster.core.network.utils.mapToEpisodes
import com.mr3y.podcaster.core.network.utils.mapToPodcast
import com.mr3y.podcaster.core.network.utils.mapToPodcasts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultPodcastsRepository @Inject constructor(
    private val podcastsDao: PodcastsDao,
    private val recentSearchesDao: RecentSearchesDao,
    private val networkClient: PodcastIndexClient
) : PodcastsRepository {

    override fun getSubscriptions() = podcastsDao.getAllPodcasts()

    override fun getEpisodesForPodcasts(podcastsIds: Set<Long>, limit: Long) = podcastsDao.getEpisodesForPodcasts(podcastsIds, limit)

    override fun getPodcast(podcastId: Long): Flow<Result<Podcast, Any>> {
        return podcastsDao.getPodcast(podcastId)
            .map { podcast ->
                if (podcast != null) {
                    Ok(podcast)
                } else {
                    networkClient.getPodcastById(podcastId).map { it.mapToPodcast() }
                }
            }
    }

    override fun getEpisodesForPodcast(podcastId: Long, podcastTitle: String, podcastArtworkUrl: String): Flow<Result<List<Episode>, Any>> {
        return podcastsDao.getEpisodesForPodcast(podcastId)
            .map { episodes ->
                if (episodes.isNotEmpty()) {
                    Ok(episodes)
                } else {
                    networkClient.getEpisodesByPodcastId(podcastId).map { it.mapToEpisodes(podcastTitle, podcastArtworkUrl) }
                }
            }
    }

    override fun getEpisode(episodeId: Long, podcastArtworkUrl: String): Flow<Result<Episode, Any>> {
        return podcastsDao.getEpisode(episodeId)
            .map { episode ->
                if (episode != null) {
                    Ok(episode)
                } else {
                    networkClient.getEpisodeById(episodeId).map { it.mapToEpisode(null, podcastArtworkUrl) }
                }
            }
    }

    override fun getDownloadedEpisodes(): Flow<List<Episode>> = podcastsDao.getDownloadedEpisodes()

    override fun downloadEpisode(episodeId: Long) {
        TODO("Not yet implemented")
    }

    override fun cancelDownloadingEpisode(episodeId: Long) {
        TODO("Not yet implemented")
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

    override fun addNewSearchQuery(searchQuery: String) = recentSearchesDao.addNewSearchQuery(searchQuery)

    override fun deleteSearchQuery(searchQuery: String) = recentSearchesDao.deleteSearchQuery(searchQuery)

    override suspend fun refreshPodcast(podcastId: Long): Boolean {
        return networkClient.getPodcastById(podcastId)
            .map {
                val podcast = it.mapToPodcast()
                podcastsDao.upsertPodcast(podcast)
                podcastsDao.updateEpisodesPodcastTitle(title = podcast.title, podcastId = podcast.id)
            }.mapBoth(
                success = { true },
                failure = { false }
            )
    }

    override suspend fun refreshEpisodesForPodcast(podcastId: Long, podcastTitle: String, podcastArtworkUrl: String): Boolean {
        return networkClient.getEpisodesByPodcastId(podcastId)
            .map {
                it.mapToEpisodes(podcastTitle, podcastArtworkUrl).forEach { episode ->
                    podcastsDao.upsertEpisode(episode)
                }
            }.mapBoth(
                success = { true },
                failure = { false }
            )
    }

    override suspend fun refreshEpisode(episodeId: Long, podcastArtworkUrl: String): Boolean {
        return networkClient.getEpisodeById(episodeId)
            .map {
                podcastsDao.upsertEpisode(it.mapToEpisode(null, podcastArtworkUrl))
            }.mapBoth(
                success = { true },
                failure = { false }
            )
    }
}
