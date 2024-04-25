package com.mr3y.podcaster.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mr3y.podcaster.core.sampledata.Episodes
import com.mr3y.podcaster.core.sampledata.EpisodesWithDownloadMetadata
import com.mr3y.podcaster.core.sampledata.Podcasts
import com.mr3y.podcaster.ui.presenter.subscriptions.SubscriptionsUIState
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@Category(ScreenshotTests::class)
class SubscriptionsScreenshotTest : BaseScreenshotTest() {

    @Test
    fun compact_subscriptionsScreen() {
        composeRule.setContent {
            PodcasterTheme(dynamicColor = false) {
                SubscriptionsScreen(
                    state = SubscriptionsUIState(
                        isSubscriptionsLoading = false,
                        isEpisodesLoading = false,
                        isRefreshing = false,
                        refreshResult = null,
                        subscriptions = Podcasts,
                        episodes = EpisodesWithDownloadMetadata,
                        queueEpisodesIds = Episodes.take(2).map { it.id },
                    ),
                    onPodcastClick = {},
                    onEpisodeClick = { _, _ -> },
                    onSettingsClick = {},
                    onNavDrawerClick = {},
                    externalContentPadding = PaddingValues(0.dp),
                    excludedWindowInsets = null,
                    currentlyPlayingEpisode = null,
                    eventSink = {},
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        takeScreenshot()
    }
}
