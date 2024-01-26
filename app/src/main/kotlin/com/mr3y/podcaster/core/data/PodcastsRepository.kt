package com.mr3y.podcaster.core.data

import com.github.michaelbull.result.Result
import com.mr3y.podcaster.core.model.CurrentlyPlayingEpisode
import com.mr3y.podcaster.core.model.EpisodeDownloadMetadata
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.EpisodeDownloadStatus
import com.mr3y.podcaster.core.model.EpisodeWithDownloadMetadata
import com.mr3y.podcaster.core.model.PlayingStatus
import com.mr3y.podcaster.core.model.Podcast
import kotlinx.coroutines.flow.Flow

interface PodcastsRepository {

    fun getSubscriptions(): Flow<List<Podcast>>

    suspend fun getSubscriptionsNonObservable(): List<Podcast>

    fun getEpisodesForPodcasts(podcastsIds: Set<Long>, limit: Long): Flow<List<Episode>>

    fun getEpisodesWithDownloadMetadataForPodcasts(podcastsIds: Set<Long>, limit: Long): Flow<List<EpisodeWithDownloadMetadata>>

    suspend fun getPodcast(podcastId: Long, forceRefresh: Boolean): Podcast?

    suspend fun getEpisodesForPodcast(podcastId: Long, podcastTitle: String, podcastArtworkUrl: String, forceRefresh: Boolean): List<Episode>?

    fun isPodcastFromSubscriptions(podcastId: Long): Flow<Boolean>

    fun hasSubscriptions(): Flow<Boolean>

    suspend fun getEpisode(episodeId: Long, podcastArtworkUrl: String, forceRefresh: Boolean): Episode?

    fun getCurrentlyPlayingEpisode(): Flow<CurrentlyPlayingEpisode?>

    fun setCurrentlyPlayingEpisode(episode: CurrentlyPlayingEpisode)

    fun updateCurrentlyPlayingEpisodeStatus(newStatus: PlayingStatus)

    suspend fun updateCurrentlyPlayingEpisodeSpeed(newSpeed: Float)

    fun updateEpisodePlaybackProgress(progressInSec: Int?, episodeId: Long)

    fun updateEpisodeDownloadStatus(episodeId: Long, newStatus: EpisodeDownloadStatus)

    fun updateEpisodeDownloadProgress(episodeId: Long, progress: Float)

    fun getEpisodeDownloadMetadata(episodeId: Long): Flow<EpisodeDownloadMetadata?>

    fun addEpisodeOnDeviceIfNotExist(episode: Episode)

    fun markEpisodeAsCompleted(episodeId: Long)

    fun subscribeToPodcast(podcast: Podcast, episodes: List<Episode>)

    fun unSubscribeFromPodcast(podcastId: Long)

    suspend fun searchForPodcastsByTerm(text: String): Result<List<Podcast>, Any>

    suspend fun getPodcast(podcastFeedUrl: String): Result<Podcast, Any>

    fun getRecentSearchQueries(): Flow<List<String>>

    fun saveNewSearchQuery(searchQuery: String)

    fun deleteSearchQuery(searchQuery: String)

    suspend fun syncRemotePodcastWithLocal(podcastId: Long): Boolean

    suspend fun syncRemoteEpisodesForPodcastWithLocal(podcastId: Long, podcastTitle: String, podcastArtworkUrl: String): Boolean

    suspend fun syncRemoteEpisodeWithLocal(episodeId: Long, podcastArtworkUrl: String): Boolean
}
