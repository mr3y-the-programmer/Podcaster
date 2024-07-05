package com.mr3y.podcaster.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.kiwi.navigationcompose.typed.composable
import com.kiwi.navigationcompose.typed.createRoutePattern
import com.kiwi.navigationcompose.typed.navigate
import com.mr3y.podcaster.ui.presenter.PodcasterAppState
import com.mr3y.podcaster.ui.presenter.UserPreferences
import com.mr3y.podcaster.ui.screens.DownloadsScreen
import com.mr3y.podcaster.ui.screens.EpisodeDetailsScreen
import com.mr3y.podcaster.ui.screens.ExploreScreen
import com.mr3y.podcaster.ui.screens.ImportExportScreen
import com.mr3y.podcaster.ui.screens.LicensesScreen
import com.mr3y.podcaster.ui.screens.PodcastDetailsScreen
import com.mr3y.podcaster.ui.screens.SettingsScreen
import com.mr3y.podcaster.ui.screens.SubscriptionsScreen

@Composable
fun PodcasterNavGraph(
    navController: NavHostController,
    appState: PodcasterAppState,
    userPreferences: UserPreferences,
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
                appState = appState,
                contentPadding = contentPadding,
                excludedWindowInsets = excludedWindowInsets,
            )
        }
        composable<Destinations.Explore> {
            ExploreScreen(
                onPodcastClick = { podcastId -> navController.navigate(Destinations.PodcastDetails(podcastId)) },
                contentPadding = contentPadding,
                excludedWindowInsets = excludedWindowInsets,
            )
        }
        composable<Destinations.PodcastDetails> {
            PodcastDetailsScreen(
                onNavigateUp = navController::navigateUpOnce,
                onEpisodeClick = { episodeId, podcastArtworkUrl -> navController.navigate(Destinations.EpisodeDetails(episodeId, podcastArtworkUrl)) },
                appState = appState,
                contentPadding = contentPadding,
                excludedWindowInsets = excludedWindowInsets,
            )
        }
        composable<Destinations.EpisodeDetails> {
            EpisodeDetailsScreen(
                onNavigateUp = navController::navigateUpOnce,
                appState = appState,
                contentPadding = contentPadding,
                excludedWindowInsets = excludedWindowInsets,
            )
        }
        composable<Destinations.Settings> {
            SettingsScreen(
                userPreferences = userPreferences,
                externalContentPadding = contentPadding,
                excludedWindowInsets = excludedWindowInsets,
                onDownloadsClick = { navController.navigate(Destinations.Downloads) },
                onImportExportClick = { navController.navigate(Destinations.ImportExport) },
                onLicensesClick = { navController.navigate(Destinations.Licenses) },
            )
        }
        composable<Destinations.Downloads> {
            DownloadsScreen(
                onEpisodeClick = { episodeId, podcastArtworkUrl -> navController.navigate(Destinations.EpisodeDetails(episodeId, podcastArtworkUrl)) },
                onNavigateUp = navController::navigateUpOnce,
                appState = appState,
                contentPadding = contentPadding,
                excludedWindowInsets = excludedWindowInsets,
            )
        }
        composable<Destinations.Licenses> {
            LicensesScreen(
                onNavigateUp = navController::navigateUpOnce,
                externalContentPadding = contentPadding,
                excludedWindowInsets = excludedWindowInsets,
            )
        }
        composable<Destinations.ImportExport> {
            ImportExportScreen(
                onNavigateUp = navController::navigateUpOnce,
                contentPadding = contentPadding,
                excludedWindowInsets = excludedWindowInsets,
            )
        }
    }
}

/**
 * Idempotent version of [NavHostController.navigateUp] function.
 */
private fun NavHostController.navigateUpOnce() {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
        navigateUp()
    }
}
