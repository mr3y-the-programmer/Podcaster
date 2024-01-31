package com.mr3y.podcaster.core.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.mr3y.podcaster.core.data.internal.DefaultPodcastsRepository
import com.mr3y.podcaster.core.local.dao.DefaultPodcastsDao
import com.mr3y.podcaster.core.local.dao.DefaultRecentSearchesDao
import com.mr3y.podcaster.core.local.di.FakeDatabaseModule
import com.mr3y.podcaster.core.logger.TestLogger
import com.mr3y.podcaster.core.network.ModifiedPodcastFeed
import com.mr3y.podcaster.core.network.di.FakeHttpClient
import com.mr3y.podcaster.core.network.di.doCleanup
import com.mr3y.podcaster.core.network.di.enqueueMockResponse
import com.mr3y.podcaster.core.network.internal.DefaultPodcastIndexClient
import com.mr3y.podcaster.ui.preview.Podcasts
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SyncDataTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val database = FakeDatabaseModule.provideInMemoryDatabaseInstance()
    private val httpClient = FakeHttpClient.getInstance()
    private val podcastsDao = DefaultPodcastsDao(database, testDispatcher)

    private lateinit var sut: DefaultPodcastsRepository

    @Before
    fun setUp() {
        sut = DefaultPodcastsRepository(
            podcastsDao = podcastsDao,
            recentSearchesDao = DefaultRecentSearchesDao(database, testDispatcher),
            networkClient = DefaultPodcastIndexClient(httpClient, TestLogger()),
        )
    }

    @Test
    fun `test refreshing podcast info is working as expected`() = runTest(testDispatcher) {
        // expect subscriptions are refreshed.
        val podcast = Podcasts[0]
        podcastsDao.upsertPodcast(podcast)
        httpClient.enqueueMockResponse(ModifiedPodcastFeed, HttpStatusCode.OK)

        val syncResult = sut.syncRemotePodcastWithLocal(podcast.id)
        assertThat(syncResult).isTrue()
        assertThat(podcastsDao.getPodcast(podcast.id)).isNotNull().isEqualTo(podcast.copy(title = "Fragmented"))

        // Reset
        podcastsDao.deletePodcast(podcast.id)

        // but don't refresh podcast if it is not from subscriptions
        val newSyncResult = sut.syncRemotePodcastWithLocal(podcast.id)
        assertThat(newSyncResult).isTrue()
        assertThat(podcastsDao.getPodcast(podcast.id)).isNull()
    }

    @After
    fun cleanUp() {
        httpClient.doCleanup()
    }
}
