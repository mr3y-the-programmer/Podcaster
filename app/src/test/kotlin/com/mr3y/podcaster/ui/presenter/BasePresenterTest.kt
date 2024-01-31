package com.mr3y.podcaster.ui.presenter

import com.mr3y.podcaster.core.data.internal.DefaultPodcastsRepository
import com.mr3y.podcaster.core.local.dao.DefaultPodcastsDao
import com.mr3y.podcaster.core.local.dao.DefaultRecentSearchesDao
import com.mr3y.podcaster.core.local.di.FakeDatabaseModule
import com.mr3y.podcaster.core.network.di.FakeHttpClient
import com.mr3y.podcaster.core.network.internal.DefaultPodcastIndexClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
open class BasePresenterTest<Event : Any> {

    protected val testDispatcher = UnconfinedTestDispatcher()
    private val database = FakeDatabaseModule.provideInMemoryDatabaseInstance()
    private val httpClient = FakeHttpClient.getInstance()
    protected val repository = DefaultPodcastsRepository(
        podcastsDao = DefaultPodcastsDao(database, testDispatcher),
        recentSearchesDao = DefaultRecentSearchesDao(database, testDispatcher),
        networkClient = DefaultPodcastIndexClient(httpClient),
    )
    protected val events = MutableSharedFlow<Event>(extraBufferCapacity = 20)
}
