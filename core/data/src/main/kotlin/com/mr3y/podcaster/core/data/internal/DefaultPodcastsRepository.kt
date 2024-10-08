package com.mr3y.podcaster.core.data.internal

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapBoth
import com.mr3y.podcaster.core.data.PodcastsRepository
import com.mr3y.podcaster.core.local.dao.PodcastsDao
import com.mr3y.podcaster.core.local.dao.RecentSearchesDao
import com.mr3y.podcaster.core.model.CurrentlyPlayingEpisode
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.EpisodeDownloadMetadata
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
    private val networkClient: PodcastIndexClient,
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
                failure = { null },
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
                    failure = { null },
                )
        } else {
            podcastsDao.getEpisodesForPodcast(podcastId)
        }
    }

    override fun isPodcastFromSubscriptions(podcastId: Long): Flow<Boolean> {
        return podcastsDao.isPodcastAvailable(podcastId)
    }

    override fun isPodcastFromSubscriptionsNonObservable(podcastId: Long): Boolean {
        return podcastsDao.isPodcastAvailableNonObservable(podcastId)
    }

    override fun countSubscriptions(): Flow<Long> = podcastsDao.countPodcasts()

    override fun countDownloads(): Flow<Long> = podcastsDao.countDownloads()

    override suspend fun getEpisode(episodeId: Long, podcastArtworkUrl: String, forceRefresh: Boolean): Episode? {
        suspend fun fetchFromNetworkAndRefresh(): Episode? {
            return networkClient.getEpisodeById(episodeId).mapBoth(
                success = { networkEpisode ->
                    val episode = networkEpisode.mapToEpisode(null, podcastArtworkUrl)
                    if (podcastsDao.isEpisodeAvailableNonObservable(episodeId)) {
                        podcastsDao.upsertEpisode(episode)
                    }
                    episode
                },
                failure = { null },
            )
        }

        return if (forceRefresh) {
            fetchFromNetworkAndRefresh()
        } else {
            val localEpisode = podcastsDao.getEpisodeOrNull(episodeId)
            localEpisode ?: fetchFromNetworkAndRefresh()
        }
    }

    override fun getCurrentlyPlayingEpisode(): Flow<CurrentlyPlayingEpisode?> = podcastsDao.getCurrentlyPlayingEpisode()

    override fun getCurrentlyPlayingEpisodeNonObservable(): CurrentlyPlayingEpisode? = podcastsDao.getCurrentlyPlayingEpisodeNonObservable()

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

    override fun updateEpisodeDuration(durationInSec: Int?, episodeId: Long) {
        podcastsDao.updateEpisodeDuration(durationInSec, episodeId)
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

    override fun getEpisodeFromQueue(episodeId: Long): Episode = podcastsDao.getEpisode(episodeId)

    override fun getQueueEpisodesIds(): Flow<List<Long>> = podcastsDao.getQueueEpisodesIds()

    override fun getQueueEpisodes(): List<Episode> = podcastsDao.getQueueEpisodes()

    override fun getFavouriteEpisodes(): Flow<List<Episode>> = podcastsDao.getFavouriteEpisodes()

    override fun countFavouriteEpisodes() = podcastsDao.countFavouriteEpisodes()

    override fun addEpisodeToQueue(episode: Episode) {
        podcastsDao.addEpisodeToQueue(episode)
    }

    override fun replaceEpisodeInQueue(newEpisode: Episode, oldEpisodeId: Long) {
        podcastsDao.replaceEpisodeInQueue(newEpisode, oldEpisodeId)
    }

    override fun removeEpisodeFromQueue(episodeId: Long) {
        podcastsDao.removeEpisodeFromQueue(episodeId)
    }

    override fun isEpisodeInQueue(episodeId: Long): Boolean = podcastsDao.isEpisodeInQueue(episodeId)

    override fun deleteAllInQueueExcept(episodesIds: Set<Long>) = podcastsDao.deleteAllInQueueExcept(episodesIds)

    override fun toggleEpisodeFavouriteStatus(isFavourite: Boolean, episode: Episode) = podcastsDao.toggleEpisodeFavouriteStatus(isFavourite, episode)

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
                failure = { false },
            )
    }

    override suspend fun syncRemoteEpisodesForPodcastWithLocal(podcastId: Long, fallbackPodcastTitle: String, fallbackPodcastArtworkUrl: String): Boolean {
        return networkClient.getEpisodesByPodcastId(podcastId)
            .map {
                val (title, artworkUrl) = podcastsDao.getPodcast(podcastId).let { podcast -> podcast?.title to podcast?.artworkUrl }
                val episodes = it.mapToEpisodes(title ?: fallbackPodcastTitle, artworkUrl ?: fallbackPodcastArtworkUrl)
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
                failure = { false },
            )
    }
}
