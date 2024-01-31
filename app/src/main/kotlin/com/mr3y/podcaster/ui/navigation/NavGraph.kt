package com.mr3y.podcaster.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.kiwi.navigationcompose.typed.composable
import com.kiwi.navigationcompose.typed.createRoutePattern
import com.kiwi.navigationcompose.typed.navigate
import com.mr3y.podcaster.ui.presenter.PodcasterAppState
import com.mr3y.podcaster.ui.screens.DownloadsScreen
import com.mr3y.podcaster.ui.screens.EpisodeDetailsScreen
import com.mr3y.podcaster.ui.screens.ExploreScreen
import com.mr3y.podcaster.ui.screens.PodcastDetailsScreen
import com.mr3y.podcaster.ui.screens.SettingsScreen
import com.mr3y.podcaster.ui.screens.SubscriptionsScreen
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
@Composable
fun PodcasterNavGraph(
    navController: NavHostController,
    onNavDrawerClick: () -> Unit,
    appState: PodcasterAppState,
    contentPadding: PaddingValues,
    excludedWindowInsets: WindowInsets?,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = createRoutePattern<Destinations.Subscriptions>(),
        modifier = modifier,
    ) {
        composable<Destinations.Subscriptions> {
            SubscriptionsScreen(
                onPodcastClick = { podcastId -> navController.navigate(Destinations.PodcastDetails(podcastId)) },
                onEpisodeClick = { episodeId, artworkUrl -> navController.navigate(Destinations.EpisodeDetails(episodeId, artworkUrl)) },
                onNavDrawerClick = onNavDrawerClick,
                onSettingsClick = { navController.navigate(Destinations.Settings) },
                appState = appState,
                contentPadding = contentPadding,
                excludedWindowInsets = excludedWindowInsets,
            )
        }
        composable<Destinations.Explore> {
            ExploreScreen(
                onPodcastClick = { podcastId -> navController.navigate(Destinations.PodcastDetails(podcastId)) },
                onNavDrawerClick = onNavDrawerClick,
                contentPadding = contentPadding,
                excludedWindowInsets = excludedWindowInsets,
            )
        }
        composable<Destinations.PodcastDetails> {
            PodcastDetailsScreen(
                onNavigateUp = navController::navigateUp,
                onEpisodeClick = { episodeId, podcastArtworkUrl -> navController.navigate(Destinations.EpisodeDetails(episodeId, podcastArtworkUrl)) },
                appState = appState,
                contentPadding = contentPadding,
                excludedWindowInsets = excludedWindowInsets,
            )
        }
        composable<Destinations.EpisodeDetails> {
            EpisodeDetailsScreen(
                onNavigateUp = navController::navigateUp,
                appState = appState,
                contentPadding = contentPadding,
                excludedWindowInsets = excludedWindowInsets,
            )
        }
        composable<Destinations.Settings> {
            SettingsScreen(
                onNavigateUp = navController::navigateUp,
                onDownloadsClick = { /*TODO*/ },
                onFeedbackClick = { /*TODO*/ },
            )
        }
        composable<Destinations.Downloads> {
            DownloadsScreen(
                onEpisodeClick = { episodeId, podcastArtworkUrl -> navController.navigate(Destinations.EpisodeDetails(episodeId, podcastArtworkUrl)) },
                onNavDrawerClick = onNavDrawerClick,
                appState = appState,
                contentPadding = contentPadding,
                excludedWindowInsets = excludedWindowInsets,
            )
        }
    }
}
