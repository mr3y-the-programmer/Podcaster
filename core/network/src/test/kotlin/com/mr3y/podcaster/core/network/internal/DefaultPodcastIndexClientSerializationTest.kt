package com.mr3y.podcaster.core.network.internal

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.github.michaelbull.result.Ok
import com.mr3y.podcaster.core.logger.TestLogger
import com.mr3y.podcaster.core.network.AndroidSearchQueryResponse
import com.mr3y.podcaster.core.network.TechnologySearchQueryResponse
import com.mr3y.podcaster.core.network.di.FakeHttpClient
import com.mr3y.podcaster.core.network.di.doCleanup
import com.mr3y.podcaster.core.network.di.enqueueMockResponse
import com.mr3y.podcaster.core.network.model.NetworkPodcasts
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class DefaultPodcastIndexClientSerializationTest {

    private val httpClient = FakeHttpClient.getInstance()

    private lateinit var sut: DefaultPodcastIndexClient

    @Before
    fun setUp() {
        sut = DefaultPodcastIndexClient(httpClient, TestLogger())
    }

    @Test
    fun `test deserializing search for podcasts response is working as expected`() = runTest {
        // Given a 200 successful response with some android-related podcasts info in the json response.
        httpClient.enqueueMockResponse(AndroidSearchQueryResponse, HttpStatusCode.OK)
        var searchResult = sut.searchForPodcastsByTerm("android")

        // then the response should be deserialized successfully.
        assertThat(searchResult).all {
            isInstanceOf<Ok<NetworkPodcasts>>()
            val responseSize = (searchResult as Ok<NetworkPodcasts>).value.count
            assertThat(responseSize).isEqualTo(60)
        }
        // Reset
        httpClient.doCleanup()

        // Repeat the same steps but on a different search query.
        httpClient.enqueueMockResponse(TechnologySearchQueryResponse, HttpStatusCode.OK)

        searchResult = sut.searchForPodcastsByTerm("technology")
        assertThat(searchResult).all {
            isInstanceOf<Ok<NetworkPodcasts>>()
            val responseSize = (searchResult as Ok<NetworkPodcasts>).value.count
            assertThat(responseSize).isEqualTo(60)
        }
    }

    @After
    fun cleanUp() {
        httpClient.doCleanup()
    }
}
