package com.mr3y.podcaster.ui.presenter.podcastdetails

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.mr3y.podcaster.ui.presenter.BasePresenterTest
import com.mr3y.podcaster.ui.preview.Episodes
import com.mr3y.podcaster.ui.preview.Podcasts
import kotlinx.coroutines.test.runTest
import org.junit.Test

class PodcastDetailsPresenterTest : BasePresenterTest<PodcastDetailsUIEvent>() {

    @Test
    fun `test podcast details presenter state`() = runTest(testDispatcher) {
        // Setup, subscribe to random podcast with some random episodes
        val randomEpisodes = Episodes.slice(0..1).map { it.copy(podcastId = Podcasts[0].id) }
        repository.subscribeToPodcast(Podcasts[0], randomEpisodes)

        moleculeFlow(RecompositionMode.Immediate) {
            PodcastDetailsPresenter(podcastId = Podcasts[0].id, repository = repository, events = events)
        }.test {
            val initialState = awaitItem()
            assertThat(initialState.isPodcastLoading).isTrue()
            assertThat(initialState.isEpisodesLoading).isTrue()
            assertThat(initialState.isRefreshing).isFalse()
            assertThat(initialState.podcast).isNull()
            assertThat(initialState.episodes).isNull()
            assertThat(initialState.subscriptionState).isEqualTo(SubscriptionState.NotSubscribed)
            assertThat(initialState.isSubscriptionStateInEditMode).isTrue()

            // On the next frame
            var currentState = awaitItem()
            assertThat(currentState.isPodcastLoading).isFalse()
            assertThat(currentState.isEpisodesLoading).isFalse()
            assertThat(currentState.isRefreshing).isFalse()
            assertThat(currentState.podcast).isNotNull().isEqualTo(Podcasts[0])
            assertThat(currentState.episodes).isNotNull().hasSize(2)
            assertThat(currentState.subscriptionState).isEqualTo(SubscriptionState.Subscribed)
            assertThat(currentState.isSubscriptionStateInEditMode).isFalse()

            events.tryEmit(PodcastDetailsUIEvent.UnSubscribe)
            currentState = awaitItem()
            assertThat(currentState.subscriptionState).isEqualTo(SubscriptionState.Subscribed)
            assertThat(currentState.isSubscriptionStateInEditMode).isTrue()

            currentState = awaitItem()
            assertThat(currentState.subscriptionState).isEqualTo(SubscriptionState.NotSubscribed)
            assertThat(currentState.isSubscriptionStateInEditMode).isFalse()

            events.tryEmit(PodcastDetailsUIEvent.Subscribe)
            currentState = awaitItem()
            assertThat(currentState.subscriptionState).isEqualTo(SubscriptionState.NotSubscribed)
            assertThat(currentState.isSubscriptionStateInEditMode).isTrue()

            currentState = awaitItem()
            assertThat(currentState.subscriptionState).isEqualTo(SubscriptionState.Subscribed)
            assertThat(currentState.isSubscriptionStateInEditMode).isFalse()

            expectNoEvents()
        }
    }
}
