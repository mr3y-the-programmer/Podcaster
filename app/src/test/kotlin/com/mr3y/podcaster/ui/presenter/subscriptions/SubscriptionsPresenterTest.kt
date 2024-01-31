package com.mr3y.podcaster.ui.presenter.subscriptions

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.mr3y.podcaster.ui.presenter.BasePresenterTest
import com.mr3y.podcaster.ui.preview.Episodes
import com.mr3y.podcaster.ui.preview.Podcasts
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SubscriptionsPresenterTest : BasePresenterTest<SubscriptionsUIEvent>() {

    @Test
    fun `test subscriptions presenter state`() = runTest(testDispatcher) {
        moleculeFlow(RecompositionMode.Immediate) {
            SubscriptionsPresenter(repository = repository, events = events)
        }.test {
            // Initially, our presenter is loading & preparing the state of our UI
            val initialState = awaitItem()
            assertThat(initialState.isSubscriptionsLoading).isTrue()
            assertThat(initialState.isEpisodesLoading).isTrue()
            assertThat(initialState.isRefreshing).isFalse()
            assertThat(initialState.subscriptions).isEmpty()
            assertThat(initialState.episodes).isEmpty()

            // on the next frame, loading is finished but there are no subscriptions yet.
            val emptyState = awaitItem()
            assertThat(emptyState.isSubscriptionsLoading).isFalse()
            assertThat(emptyState.isEpisodesLoading).isFalse()
            assertThat(emptyState.isRefreshing).isFalse()
            assertThat(emptyState.subscriptions).isEmpty()
            assertThat(emptyState.episodes).isEmpty()

            // subscribe to random podcast with some random episodes
            val randomEpisodes = Episodes.slice(0..1).map { it.copy(podcastId = Podcasts[0].id) }
            repository.subscribeToPodcast(Podcasts[0], randomEpisodes)

            var currentState = awaitItem()
            assertThat(currentState.isSubscriptionsLoading).isFalse()
            assertThat(currentState.isEpisodesLoading).isFalse()
            assertThat(currentState.isRefreshing).isFalse()
            assertThat(currentState.subscriptions).isEqualTo(listOf(Podcasts[0]))
            assertThat(currentState.episodes).isEmpty()
            currentState = awaitItem()
            assertThat(currentState.subscriptions).isEqualTo(listOf(Podcasts[0]))
            // each episode addition triggers separate state update, and we added 2 episodes
            assertThat(currentState.episodes).hasSize(1)
            currentState = awaitItem()
            assertThat(currentState.episodes).hasSize(2)

            expectNoEvents()
        }
    }
}
