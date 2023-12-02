package com.mr3y.podcaster.core.network

import com.github.michaelbull.result.Result
import com.mr3y.podcaster.core.network.model.NetworkEpisode
import com.mr3y.podcaster.core.network.model.NetworkEpisodes
import com.mr3y.podcaster.core.network.model.NetworkPodcasts
import com.mr3y.podcaster.core.network.model.NetworkPodcast

typealias ApiResponse<T> = Result<T, Any>

interface PodcastIndexClient {

    suspend fun searchForPodcastsByTerm(term: String): ApiResponse<NetworkPodcasts>

    suspend fun getPodcastByFeedUrl(feedUrl: String): ApiResponse<NetworkPodcast>

    suspend fun getPodcastById(podcastId: Long): ApiResponse<NetworkPodcast>

    suspend fun getEpisodesByPodcastId(podcastId: Long): ApiResponse<NetworkEpisodes>

    suspend fun getEpisodeById(episodeId: Long): ApiResponse<NetworkEpisode>
}
