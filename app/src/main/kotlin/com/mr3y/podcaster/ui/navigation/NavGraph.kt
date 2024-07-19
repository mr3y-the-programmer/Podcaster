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
import com.kiwi.navigationcompose.typed.composable
import com.kiwi.navigationcompose.typed.createRoutePattern
import com.kiwi.navigationcompose.typed.navigate
import com.kiwi.navigationcompose.typed.navigation
import com.mr3y.podcaster.ui.components.LocalAnimatedVisibilityScope
import com.mr3y.podcaster.ui.presenter.PodcasterAppState
import com.mr3y.podcaster.ui.presenter.UserPreferences
import com.mr3y.podcaster.ui.presenter.episodedetails.EpisodeDetailsViewModel
import com.mr3y.podcaster.ui.presenter.podcastdetails.PodcastDetailsViewModel
import com.mr3y.podcaster.ui.screens.DownloadsScreen
import com.mr3y.podcaster.ui.screens.EpisodeDetailsScreen
import com.mr3y.podcaster.ui.screens.ExploreScreen
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
        startDestination = createRoutePattern<Destinations.SubscriptionsGraph>(),
        modifier = modifier,
    ) {
        navigation<Destinations.SubscriptionsGraph>(
            startDestination = createRoutePattern<Destinations.Subscriptions>(),
        ) {
            animatedComposable<Destinations.Subscriptions> { _, _ ->
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
            animatedComposable<Destinations.PodcastDetailsSubscriptionsGraph> { _, destination ->
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    PodcastDetailsScreen(
                        onNavigateUp = navController::navigateUpOnce,
                        onEpisodeClick = { episodeId, podcastArtworkUrl -> navController.navigate(Destinations.EpisodeDetailsSubscriptionsGraph(episodeId, podcastArtworkUrl)) },
                        appState = appState,
                        contentPadding = contentPadding,
                        excludedWindowInsets = excludedWindowInsets,
                        viewModel = hiltViewModel<PodcastDetailsViewModel, PodcastDetailsViewModel.Factory>(
                            creationCallback = { factory -> factory.create(destination.podcastId) },
                        ),
                    )
                }
            }
            animatedComposable<Destinations.EpisodeDetailsSubscriptionsGraph> { _, destination ->
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    EpisodeDetailsScreen(
                        onNavigateUp = navController::navigateUpOnce,
                        appState = appState,
                        contentPadding = contentPadding,
                        excludedWindowInsets = excludedWindowInsets,
                        viewModel = hiltViewModel<EpisodeDetailsViewModel, EpisodeDetailsViewModel.Factory>(
                            creationCallback = { factory -> factory.create(destination.episodeId, destination.podcastArtworkUrl) },
                        ),
                    )
                }
            }
        }
        navigation<Destinations.ExploreGraph>(
            startDestination = createRoutePattern<Destinations.Explore>(),
        ) {
            animatedComposable<Destinations.Explore> { _, _ ->
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    ExploreScreen(
                        onPodcastClick = { podcastId -> navController.navigate(Destinations.PodcastDetailsExploreGraph(podcastId)) },
                        contentPadding = contentPadding,
                        excludedWindowInsets = excludedWindowInsets,
                    )
                }
            }
            animatedComposable<Destinations.PodcastDetailsExploreGraph> { _, destination ->
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    PodcastDetailsScreen(
                        onNavigateUp = navController::navigateUpOnce,
                        onEpisodeClick = { episodeId, podcastArtworkUrl -> navController.navigate(Destinations.EpisodeDetailsExploreGraph(episodeId, podcastArtworkUrl)) },
                        appState = appState,
                        contentPadding = contentPadding,
                        excludedWindowInsets = excludedWindowInsets,
                        viewModel = hiltViewModel<PodcastDetailsViewModel, PodcastDetailsViewModel.Factory>(
                            creationCallback = { factory -> factory.create(destination.podcastId) },
                        ),
                    )
                }
            }
            animatedComposable<Destinations.EpisodeDetailsExploreGraph> { _, destination ->
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    EpisodeDetailsScreen(
                        onNavigateUp = navController::navigateUpOnce,
                        appState = appState,
                        contentPadding = contentPadding,
                        excludedWindowInsets = excludedWindowInsets,
                        viewModel = hiltViewModel<EpisodeDetailsViewModel, EpisodeDetailsViewModel.Factory>(
                            creationCallback = { factory -> factory.create(destination.episodeId, destination.podcastArtworkUrl) },
                        ),
                    )
                }
            }
        }
        navigation<Destinations.LibraryGraph>(
            startDestination = createRoutePattern<Destinations.Library>(),
        ) {
            animatedComposable<Destinations.Library> { _, _ ->
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    LibraryScreen(
                        onDownloadsClick = { navController.navigate(Destinations.Downloads) },
                        externalContentPadding = contentPadding,
                        excludedWindowInsets = excludedWindowInsets,
                    )
                }
            }
            animatedComposable<Destinations.Downloads> { _, _ ->
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
            animatedComposable<Destinations.EpisodeDetailsLibraryGraph> { _, destination ->
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    EpisodeDetailsScreen(
                        onNavigateUp = navController::navigateUpOnce,
                        appState = appState,
                        contentPadding = contentPadding,
                        excludedWindowInsets = excludedWindowInsets,
                        viewModel = hiltViewModel<EpisodeDetailsViewModel, EpisodeDetailsViewModel.Factory>(
                            creationCallback = { factory -> factory.create(destination.episodeId, destination.podcastArtworkUrl) },
                        ),
                    )
                }
            }
        }
        navigation<Destinations.SettingsGraph>(
            startDestination = createRoutePattern<Destinations.Settings>(),
        ) {
            animatedComposable<Destinations.Settings> { _, _ ->
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
            animatedComposable<Destinations.Licenses> { _, _ ->
                CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                    LicensesScreen(
                        onNavigateUp = navController::navigateUpOnce,
                        externalContentPadding = contentPadding,
                        excludedWindowInsets = excludedWindowInsets,
                    )
                }
            }
            animatedComposable<Destinations.ImportExport> { _, _ ->
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
