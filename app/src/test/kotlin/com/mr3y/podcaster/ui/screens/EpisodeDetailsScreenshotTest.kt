package com.mr3y.podcaster.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mr3y.podcaster.core.sampledata.DownloadMetadata
import com.mr3y.podcaster.core.sampledata.EpisodeWithDetails
import com.mr3y.podcaster.ui.presenter.episodedetails.EpisodeDetailsUIState
import com.mr3y.podcaster.ui.theme.PodcasterTheme
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@Category(ScreenshotTests::class)
class EpisodeDetailsScreenshotTest : BaseScreenshotTest() {

    @Test
    fun compact_episodeDetailsScreen() {
        composeRule.setContent {
            PodcasterTheme(dynamicColor = false) {
                EpisodeDetailsScreen(
                    state = EpisodeDetailsUIState(
                        isLoading = false,
                        episode = EpisodeWithDetails,
                        queueEpisodesIds = emptyList(),
                        isRefreshing = false,
                        refreshResult = null,
                        downloadMetadata = DownloadMetadata,
                    ),
                    onNavigateUp = {},
                    isSelected = false,
                    playingStatus = null,
                    externalContentPadding = PaddingValues(0.dp),
                    excludedWindowInsets = null,
                    eventSink = {},
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        takeScreenshot()
    }
}
