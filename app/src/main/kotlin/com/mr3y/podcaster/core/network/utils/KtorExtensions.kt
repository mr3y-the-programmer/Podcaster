package com.mr3y.podcaster.core.network.utils

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.mr3y.podcaster.core.logger.Logger
import com.mr3y.podcaster.core.network.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.isSuccess

/**
 * Executes an [HttpClient]'s GET request with the specified [url] and
 * an optional [block] receiving an [HttpRequestBuilder] for configuring the request.
 *
 * @return [ApiResponse]
 */
suspend inline fun <reified T> HttpClient.getApiResponse(
    urlString: String,
    logger: Logger,
    block: HttpRequestBuilder.() -> Unit = {},
): ApiResponse<T> {
    return try {
        val response = get(urlString, block = block)
        if (response.status.isSuccess()) {
            Ok(response.body())
        } else {
            logger.w(tag = "DefaultPodcastIndexClient") {
                "Request failed! endpoint: $urlString, http status code: ${response.status}"
            }
            Err(response)
        }
    } catch (ex: Exception) {
        logger.e(ex, tag = "DefaultPodcastIndexClient") {
            "Exception occurred on requesting data from endpoint $urlString"
        }
        Err(ex)
    }
}
