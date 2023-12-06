package com.mr3y.podcaster.core.data

import com.github.michaelbull.result.Result
import com.mr3y.podcaster.core.model.Episode
import com.mr3y.podcaster.core.model.Podcast
import kotlinx.coroutines.flow.Flow

interface PodcastsRepository {

    fun getSubscriptions(): Flow<List<Podcast>>

    fun getEpisodesForPodcasts(podcastsIds: Set<Long>, limit: Long): Flow<List<Episode>>

    fun getPodcast(podcastId: Long): Flow<Result<Podcast, Any>>

    fun getEpisodesForPodcast(podcastId: Long): Flow<Result<List<Episode>, Any>>

    fun getEpisode(episodeId: Long): Flow<Result<Episode, Any>>

    fun getDownloadedEpisodes(): Flow<List<Episode>>

    fun downloadEpisode(episodeId: Long)

    fun cancelDownloadingEpisode(episodeId: Long)

    fun updateEpisodePlaybackProgress(progressInSec: Int?, episodeId: Long)

    fun subscribeToPodcast(podcast: Podcast, episodes: List<Episode>)

    fun unSubscribeFromPodcast(podcastId: Long)

    suspend fun searchForPodcastsByTerm(text: String): Result<List<Podcast>, Any>

    suspend fun getPodcast(podcastFeedUrl: String): Result<Podcast, Any>

    fun getRecentSearchQueries(): Flow<List<String>>

    fun addNewSearchQuery(searchQuery: String)

    fun deleteSearchQuery(searchQuery: String)

    suspend fun refreshPodcast(podcastId: Long): Boolean

    suspend fun refreshEpisodesForPodcast(podcastId: Long): Boolean

    suspend fun refreshEpisode(episodeId: Long): Boolean
}
