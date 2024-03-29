package com.mr3y.podcaster.core.network.internal

import com.mr3y.podcaster.core.logger.Logger
import com.mr3y.podcaster.core.network.ApiResponse
import com.mr3y.podcaster.core.network.PodcastIndexClient
import com.mr3y.podcaster.core.network.model.NetworkEpisode
import com.mr3y.podcaster.core.network.model.NetworkEpisodes
import com.mr3y.podcaster.core.network.model.NetworkPodcast
import com.mr3y.podcaster.core.network.model.NetworkPodcasts
import com.mr3y.podcaster.core.network.utils.getApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import javax.inject.Inject

class DefaultPodcastIndexClient @Inject constructor(
    private val httpClient: HttpClient,
    private val logger: Logger,
) : PodcastIndexClient {

    override suspend fun searchForPodcastsByTerm(term: String): ApiResponse<NetworkPodcasts> {
        return httpClient.getApiResponse("$BaseUrl/search/byterm", logger) {
            parameter("q", term)
        }
    }

    override suspend fun getPodcastByFeedUrl(feedUrl: String): ApiResponse<NetworkPodcast> {
        return httpClient.getApiResponse("$BaseUrl/podcasts/byfeedurl", logger) {
            parameter("url", feedUrl)
        }
    }

    override suspend fun getPodcastById(podcastId: Long): ApiResponse<NetworkPodcast> {
        return httpClient.getApiResponse("$BaseUrl/podcasts/byfeedid", logger) {
            parameter("id", podcastId)
        }
    }

    override suspend fun getEpisodesByPodcastId(podcastId: Long): ApiResponse<NetworkEpisodes> {
        return httpClient.getApiResponse("$BaseUrl/episodes/byfeedid?id=$podcastId&fulltext", logger)
    }

    override suspend fun getEpisodeById(episodeId: Long): ApiResponse<NetworkEpisode> {
        return httpClient.getApiResponse("$BaseUrl/episodes/byid?id=$episodeId&fulltext", logger)
    }

    companion object {
        private const val BaseUrl = "https://api.podcastindex.org/api/1.0"
    }
}
