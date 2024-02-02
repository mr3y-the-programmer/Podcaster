package com.mr3y.podcaster.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kiwi.navigationcompose.typed.createRoutePattern
import com.kiwi.navigationcompose.typed.navigate
import com.mr3y.podcaster.LocalStrings
import com.mr3y.podcaster.ui.navigation.Destinations
import com.mr3y.podcaster.ui.navigation.PodcasterNavGraph
import com.mr3y.podcaster.ui.presenter.PodcasterAppState
import com.mr3y.podcaster.ui.presenter.UserPreferences
import com.mr3y.podcaster.ui.resources.Subscriptions
import com.mr3y.podcaster.ui.theme.isStatusBarAppearanceLight
import com.mr3y.podcaster.ui.theme.setStatusBarAppearanceLight
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    appState: PodcasterAppState,
    userPreferences: UserPreferences,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isDark = isSystemInDarkTheme()
    var isStatusBarLightForCurrentScreen by rememberSaveable(Unit) { mutableStateOf(!isDark) }

    val currentlyPlayingEpisode by appState.currentlyPlayingEpisode.collectAsStateWithLifecycle()
    val isPlayerViewExpanded by appState.isPlayerViewExpanded.collectAsStateWithLifecycle()
    val trackProgress by appState.trackProgress.collectAsStateWithLifecycle()
    LifecycleStartEffect(Unit) {
        appState.initializePlayer(context)

        onStopOrDispose {
            appState.releasePlayer()
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val strings = LocalStrings.current
    val drawerTabs = listOf(
        DrawerTab(
            strings.tab_subscriptions_label,
            Icons.Outlined.Subscriptions,
            createRoutePattern<Destinations.Subscriptions>(),
            Destinations.Subscriptions,
        ),
        DrawerTab(
            strings.tab_explore_label,
            Icons.Outlined.Search,
            createRoutePattern<Destinations.Explore>(),
            Destinations.Explore,
        ),
        DrawerTab(
            strings.tab_downloads_label,
            Icons.Outlined.FileDownload,
            createRoutePattern<Destinations.Downloads>(),
            Destinations.Downloads,
        ),
    )
    LaunchedEffect(key1 = drawerState.targetValue, key2 = isPlayerViewExpanded) {
        if (drawerState.targetValue == DrawerValue.Open || isPlayerViewExpanded) {
            // Save the current status bar appearance to restore it later.
            isStatusBarLightForCurrentScreen = context.isStatusBarAppearanceLight()
            context.setStatusBarAppearanceLight(isAppearanceLight = !isDark)
        } else {
            context.setStatusBarAppearanceLight(isAppearanceLight = isStatusBarLightForCurrentScreen)
        }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = currentDestination?.route in drawerTabs.map { it.route },
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                drawerTabs.forEach { tab ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                    NavigationDrawerItem(
                        label = { Text(text = tab.label) },
                        icon = { Icon(imageVector = tab.icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface) },
                        selected = isSelected,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (!isSelected) {
                                navController.navigate(tab.destination) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                    )
                }
            }
        },
        modifier = modifier,
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
        ) {
            val density = LocalDensity.current
            val expandedPlayerViewHeight = maxHeight
            val collapsedPlayerViewHeight = 104.dp
            val collapsedPlayerViewOffset = with(density) { expandedPlayerViewHeight.toPx() - collapsedPlayerViewHeight.toPx() }

            val anchors = DraggableAnchors {
                PlayerViewState.Expanded at 0f
                PlayerViewState.Collapsed at collapsedPlayerViewOffset
            }
            val state = remember {
                AnchoredDraggableState(
                    initialValue = if (isPlayerViewExpanded) PlayerViewState.Expanded else PlayerViewState.Collapsed,
                    anchors = anchors,
                    positionalThreshold = { distance: Float -> distance * 0.1f },
                    animationSpec = spring(),
                    velocityThreshold = { with(density) { 80.dp.toPx() } },
                )
            }
            LaunchedEffect(state.currentValue) {
                when (state.currentValue) {
                    PlayerViewState.Collapsed -> {
                        if (isPlayerViewExpanded) {
                            appState.collapsePlayerView()
                        }
                    }
                    PlayerViewState.Expanded -> {
                        if (!isPlayerViewExpanded) {
                            appState.expandPlayerView()
                        }
                    }
                }
            }
            val playerViewBottomInsets = WindowInsets.navigationBars
            PodcasterNavGraph(
                navController = navController,
                onNavDrawerClick = {
                    scope.launch { drawerState.open() }
                },
                appState = appState,
                userPreferences = userPreferences,
                contentPadding = PaddingValues(bottom = if (currentlyPlayingEpisode != null) collapsedPlayerViewHeight else 0.dp),
                excludedWindowInsets = if (currentlyPlayingEpisode != null) playerViewBottomInsets else null,
            )
            currentlyPlayingEpisode?.let { activeEpisode ->
                val isCollapsed = state.targetValue == PlayerViewState.Collapsed
                val containerColor by animateColorAsState(
                    targetValue = if (isCollapsed) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
                    label = "PlayerViewColorAnimation",
                )
                Crossfade(
                    targetState = isCollapsed,
                    label = "Animated PlayerView",
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .height(expandedPlayerViewHeight)
                        .offset {
                            IntOffset(
                                0,
                                state
                                    .requireOffset()
                                    .toInt(),
                            )
                        }
                        .anchoredDraggable(state, Orientation.Vertical),
                ) { collapsed ->
                    if (collapsed) {
                        CollapsedPlayerView(
                            currentlyPlayingEpisode = activeEpisode,
                            onResume = appState::resume,
                            onPause = appState::pause,
                            progress = trackProgress,
                            contentWindowInsets = playerViewBottomInsets,
                            containerColor = containerColor,
                        )
                    } else {
                        ExpandedPlayerView(
                            currentlyPlayingEpisode = activeEpisode,
                            onResume = appState::resume,
                            onPause = appState::pause,
                            onForward = appState::forward,
                            onReplay = appState::replay,
                            onPlaybackSpeedChange = appState::changePlaybackSpeed,
                            progress = trackProgress,
                            onSeeking = appState::seekTo,
                            onBack = { scope.launch { state.animateTo(PlayerViewState.Collapsed) } },
                            containerColor = containerColor,
                        )
                    }
                }
            }
        }
    }
}

data class DrawerTab(
    val label: String,
    val icon: ImageVector,
    val route: String,
    val destination: Destinations,
)

private enum class PlayerViewState {
    Expanded,
    Collapsed,
}
