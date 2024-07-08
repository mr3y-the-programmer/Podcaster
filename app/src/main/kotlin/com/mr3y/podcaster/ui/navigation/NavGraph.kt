package com.mr3y.podcaster.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.kiwi.navigationcompose.typed.composable
import com.kiwi.navigationcompose.typed.createRoutePattern
import com.kiwi.navigationcompose.typed.navigate
import com.kiwi.navigationcompose.typed.navigation
import com.mr3y.podcaster.ui.presenter.PodcasterAppState
import com.mr3y.podcaster.ui.presenter.UserPreferences
import com.mr3y.podcaster.ui.presenter.episodedetails.EpisodeDetailsViewModel
import com.mr3y.podcaster.ui.presenter.podcastdetails.PodcastDetailsViewModel
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
        startDestination = createRoutePattern<Destinations.SubscriptionsGraph>(),
        modifier = modifier,
    ) {
        navigation<Destinations.SubscriptionsGraph>(
            startDestination = createRoutePattern<Destinations.Subscriptions>()
        ) {
            composable<Destinations.Subscriptions> {
                SubscriptionsScreen(
                    onPodcastClick = { podcastId -> navController.navigate(Destinations.PodcastDetailsSubscriptionsGraph(podcastId)) },
                    onEpisodeClick = { episodeId, artworkUrl -> navController.navigate(Destinations.EpisodeDetailsSubscriptionsGraph(episodeId, artworkUrl)) },
                    appState = appState,
                    contentPadding = contentPadding,
                    excludedWindowInsets = excludedWindowInsets,
                )
            }
            composable<Destinations.PodcastDetailsSubscriptionsGraph> {
                PodcastDetailsScreen(
                    onNavigateUp = navController::navigateUpOnce,
                    onEpisodeClick = { episodeId, podcastArtworkUrl -> navController.navigate(Destinations.EpisodeDetailsSubscriptionsGraph(episodeId, podcastArtworkUrl)) },
                    appState = appState,
                    contentPadding = contentPadding,
                    excludedWindowInsets = excludedWindowInsets,
                    viewModel = hiltViewModel<PodcastDetailsViewModel, PodcastDetailsViewModel.Factory>(
                        creationCallback = { factory -> factory.create(podcastId) }
                    )
                )
            }
            composable<Destinations.EpisodeDetailsSubscriptionsGraph> {
                EpisodeDetailsScreen(
                    onNavigateUp = navController::navigateUpOnce,
                    appState = appState,
                    contentPadding = contentPadding,
                    excludedWindowInsets = excludedWindowInsets,
                    viewModel = hiltViewModel<EpisodeDetailsViewModel, EpisodeDetailsViewModel.Factory>(
                        creationCallback = { factory -> factory.create(episodeId, podcastArtworkUrl) }
                    )
                )
            }
        }
        navigation<Destinations.ExploreGraph>(
            startDestination = createRoutePattern<Destinations.Explore>()
        ) {
            composable<Destinations.Explore> {
                ExploreScreen(
                    onPodcastClick = { podcastId -> navController.navigate(Destinations.PodcastDetailsExploreGraph(podcastId)) },
                    contentPadding = contentPadding,
                    excludedWindowInsets = excludedWindowInsets,
                )
            }
            composable<Destinations.PodcastDetailsExploreGraph> {
                PodcastDetailsScreen(
                    onNavigateUp = navController::navigateUpOnce,
                    onEpisodeClick = { episodeId, podcastArtworkUrl -> navController.navigate(Destinations.EpisodeDetailsExploreGraph(episodeId, podcastArtworkUrl)) },
                    appState = appState,
                    contentPadding = contentPadding,
                    excludedWindowInsets = excludedWindowInsets,
                    viewModel = hiltViewModel<PodcastDetailsViewModel, PodcastDetailsViewModel.Factory>(
                        creationCallback = { factory -> factory.create(podcastId) }
                    )
                )
            }
            composable<Destinations.EpisodeDetailsExploreGraph> {
                EpisodeDetailsScreen(
                    onNavigateUp = navController::navigateUpOnce,
                    appState = appState,
                    contentPadding = contentPadding,
                    excludedWindowInsets = excludedWindowInsets,
                    viewModel = hiltViewModel<EpisodeDetailsViewModel, EpisodeDetailsViewModel.Factory>(
                        creationCallback = { factory -> factory.create(episodeId, podcastArtworkUrl) }
                    )
                )
            }
        }

        navigation<Destinations.SettingsGraph>(
            startDestination = createRoutePattern<Destinations.Settings>()
        ) {
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
                    onEpisodeClick = { episodeId, podcastArtworkUrl -> navController.navigate(Destinations.EpisodeDetailsDownloadsGraph(episodeId, podcastArtworkUrl)) },
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
            composable<Destinations.EpisodeDetailsDownloadsGraph> {
                EpisodeDetailsScreen(
                    onNavigateUp = navController::navigateUpOnce,
                    appState = appState,
                    contentPadding = contentPadding,
                    excludedWindowInsets = excludedWindowInsets,
                    viewModel = hiltViewModel<EpisodeDetailsViewModel, EpisodeDetailsViewModel.Factory>(
                        creationCallback = { factory -> factory.create(episodeId, podcastArtworkUrl) }
                    )
                )
            }
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
