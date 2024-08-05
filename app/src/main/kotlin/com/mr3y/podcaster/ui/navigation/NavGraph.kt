package com.mr3y.podcaster.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.mr3y.podcaster.ui.components.LocalAnimatedVisibilityScope
import com.mr3y.podcaster.ui.presenter.PodcasterAppState
import com.mr3y.podcaster.ui.presenter.UserPreferences
import com.mr3y.podcaster.ui.presenter.episodedetails.EpisodeDetailsViewModel
import com.mr3y.podcaster.ui.presenter.podcastdetails.PodcastDetailsViewModel
import com.mr3y.podcaster.ui.screens.DownloadsScreen
import com.mr3y.podcaster.ui.screens.EpisodeDetailsScreen
import com.mr3y.podcaster.ui.screens.ExploreScreen
import com.mr3y.podcaster.ui.screens.FavoritesScreen
import com.mr3y.podcaster.ui.screens.ImportExportScreen
import com.mr3y.podcaster.ui.screens.LibraryScreen
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
        startDestination = Destinations.SubscriptionsGraph,
        modifier = modifier,
    ) {
        navigation<Destinations.SubscriptionsGraph>(
            startDestination = Destinations.Subscriptions,
        ) {
            composable<Destinations.Subscriptions> {
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    SubscriptionsScreen(
                        onPodcastClick = { podcastId -> navController.navigate(Destinations.PodcastDetailsSubscriptionsGraph(podcastId)) },
                        onEpisodeClick = { episodeId, artworkUrl -> navController.navigate(Destinations.EpisodeDetailsSubscriptionsGraph(episodeId, artworkUrl)) },
                        appState = appState,
                        contentPadding = contentPadding,
                        excludedWindowInsets = excludedWindowInsets,
                    )
                }
            }
            composable<Destinations.PodcastDetailsSubscriptionsGraph> { navBackStackEntry ->
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    PodcastDetailsScreen(
                        onNavigateUp = navController::navigateUpOnce,
                        onEpisodeClick = { episodeId, podcastArtworkUrl -> navController.navigate(Destinations.EpisodeDetailsSubscriptionsGraph(episodeId, podcastArtworkUrl)) },
                        appState = appState,
                        contentPadding = contentPadding,
                        excludedWindowInsets = excludedWindowInsets,
                        viewModel = hiltViewModel<PodcastDetailsViewModel, PodcastDetailsViewModel.Factory>(
                            creationCallback = { factory -> factory.create(navBackStackEntry.toRoute<Destinations.PodcastDetailsSubscriptionsGraph>().podcastId) },
                        ),
                    )
                }
            }
            composable<Destinations.EpisodeDetailsSubscriptionsGraph> { navBackStackEntry ->
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    EpisodeDetailsScreen(
                        onNavigateUp = navController::navigateUpOnce,
                        appState = appState,
                        contentPadding = contentPadding,
                        excludedWindowInsets = excludedWindowInsets,
                        viewModel = hiltViewModel<EpisodeDetailsViewModel, EpisodeDetailsViewModel.Factory>(
                            creationCallback = { factory ->
                                val destination = navBackStackEntry.toRoute<Destinations.EpisodeDetailsSubscriptionsGraph>()
                                factory.create(destination.episodeId, destination.podcastArtworkUrl)
                            },
                        ),
                    )
                }
            }
        }
        navigation<Destinations.ExploreGraph>(
            startDestination = Destinations.Explore,
        ) {
            composable<Destinations.Explore> {
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    ExploreScreen(
                        onPodcastClick = { podcastId -> navController.navigate(Destinations.PodcastDetailsExploreGraph(podcastId)) },
                        contentPadding = contentPadding,
                        excludedWindowInsets = excludedWindowInsets,
                    )
                }
            }
            composable<Destinations.PodcastDetailsExploreGraph> { navBackStackEntry ->
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    PodcastDetailsScreen(
                        onNavigateUp = navController::navigateUpOnce,
                        onEpisodeClick = { episodeId, podcastArtworkUrl -> navController.navigate(Destinations.EpisodeDetailsExploreGraph(episodeId, podcastArtworkUrl)) },
                        appState = appState,
                        contentPadding = contentPadding,
                        excludedWindowInsets = excludedWindowInsets,
                        viewModel = hiltViewModel<PodcastDetailsViewModel, PodcastDetailsViewModel.Factory>(
                            creationCallback = { factory -> factory.create(navBackStackEntry.toRoute<Destinations.PodcastDetailsExploreGraph>().podcastId) },
                        ),
                    )
                }
            }
            composable<Destinations.EpisodeDetailsExploreGraph> { navBackStackEntry ->
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    EpisodeDetailsScreen(
                        onNavigateUp = navController::navigateUpOnce,
                        appState = appState,
                        contentPadding = contentPadding,
                        excludedWindowInsets = excludedWindowInsets,
                        viewModel = hiltViewModel<EpisodeDetailsViewModel, EpisodeDetailsViewModel.Factory>(
                            creationCallback = { factory ->
                                val destination = navBackStackEntry.toRoute<Destinations.EpisodeDetailsExploreGraph>()
                                factory.create(destination.episodeId, destination.podcastArtworkUrl)
                            },
                        ),
                    )
                }
            }
        }
        navigation<Destinations.LibraryGraph>(
            startDestination = Destinations.Library,
        ) {
            composable<Destinations.Library> {
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    LibraryScreen(
                        onDownloadsClick = { navController.navigate(Destinations.Downloads) },
                        onFavoritesClick = { navController.navigate(Destinations.Favorites) },
                        externalContentPadding = contentPadding,
                        excludedWindowInsets = excludedWindowInsets,
                    )
                }
            }
            composable<Destinations.Downloads> {
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    DownloadsScreen(
                        onEpisodeClick = { episodeId, podcastArtworkUrl -> navController.navigate(Destinations.EpisodeDetailsLibraryGraph(episodeId, podcastArtworkUrl)) },
                        onNavigateUp = navController::navigateUpOnce,
                        appState = appState,
                        contentPadding = contentPadding,
                        excludedWindowInsets = excludedWindowInsets,
                    )
                }
            }
            composable<Destinations.Favorites> {
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    FavoritesScreen(
                        onEpisodeClick = { episodeId, podcastArtworkUrl -> navController.navigate(Destinations.EpisodeDetailsLibraryGraph(episodeId, podcastArtworkUrl)) },
                        onNavigateUp = navController::navigateUpOnce,
                        contentPadding = contentPadding,
                        excludedWindowInsets = excludedWindowInsets
                    )
                }
            }
            composable<Destinations.EpisodeDetailsLibraryGraph> { navBackStackEntry ->
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    EpisodeDetailsScreen(
                        onNavigateUp = navController::navigateUpOnce,
                        appState = appState,
                        contentPadding = contentPadding,
                        excludedWindowInsets = excludedWindowInsets,
                        viewModel = hiltViewModel<EpisodeDetailsViewModel, EpisodeDetailsViewModel.Factory>(
                            creationCallback = { factory ->
                                val destination = navBackStackEntry.toRoute<Destinations.EpisodeDetailsLibraryGraph>()
                                factory.create(destination.episodeId, destination.podcastArtworkUrl)
                            },
                        ),
                    )
                }
            }
        }
        navigation<Destinations.SettingsGraph>(
            startDestination = Destinations.Settings,
        ) {
            composable<Destinations.Settings> {
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    SettingsScreen(
                        userPreferences = userPreferences,
                        externalContentPadding = contentPadding,
                        excludedWindowInsets = excludedWindowInsets,
                        onImportExportClick = { navController.navigate(Destinations.ImportExport) },
                        onLicensesClick = { navController.navigate(Destinations.Licenses) },
                    )
                }
            }
            composable<Destinations.Licenses> {
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    LicensesScreen(
                        onNavigateUp = navController::navigateUpOnce,
                        externalContentPadding = contentPadding,
                        excludedWindowInsets = excludedWindowInsets,
                    )
                }
            }
            composable<Destinations.ImportExport> {
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    ImportExportScreen(
                        onNavigateUp = navController::navigateUpOnce,
                        contentPadding = contentPadding,
                        excludedWindowInsets = excludedWindowInsets,
                    )
                }
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
