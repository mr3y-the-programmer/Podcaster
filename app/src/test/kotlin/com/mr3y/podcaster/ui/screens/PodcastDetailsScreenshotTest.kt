package com.mr3y.podcaster.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import com.mr3y.podcaster.core.sampledata.Episodes
import com.mr3y.podcaster.core.sampledata.PodcastWithDetails
import com.mr3y.podcaster.ui.presenter.podcastdetails.PodcastDetailsUIState
import com.mr3y.podcaster.ui.presenter.podcastdetails.SubscriptionState
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@Category(ScreenshotTests::class)
class PodcastDetailsScreenshotTest : BaseScreenshotTest() {

    @Test
    fun compact_notSubscribed_podcastDetailsScreen() {
        composeRule.setContent {
            PodcasterTheme(dynamicColor = false) {
                PodcastDetailsScreen(
                    state = PodcastDetailsUIState(
                        isPodcastLoading = false,
                        isEpisodesLoading = false,
                        podcast = PodcastWithDetails,
                        subscriptionState = SubscriptionState.NotSubscribed,
                        isSubscriptionStateInEditMode = false,
                        episodes = Episodes.take(4),
                        isRefreshing = false,
                        refreshResult = null,
                        queueEpisodesIds = Episodes.take(1).map { it.id },
                    ),
                    onNavigateUp = {},
                    onSubscribe = {},
                    onUnsubscribe = {},
                    onRefresh = {},
                    onPlayEpisode = {},
                    onPause = {},
                    onAddEpisodeToQueue = {},
                    onRemoveEpisodeFromQueue = {},
                    currentlyPlayingEpisode = null,
                    onConsumeErrorPlayingStatus = {},
                    externalContentPadding = PaddingValues(0.dp),
                    excludedWindowInsets = null,
                    onConsumeResult = {},
                    onRetry = {},
                    onEpisodeClick = { _, _ -> },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        composeRule.onRoot().captureRoboImage()
    }
}
