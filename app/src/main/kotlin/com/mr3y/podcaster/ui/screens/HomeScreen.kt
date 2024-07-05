package com.mr3y.podcaster.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
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
import com.mr3y.podcaster.ui.theme.isAppThemeDark
import com.mr3y.podcaster.ui.theme.isStatusBarAppearanceLight
import com.mr3y.podcaster.ui.theme.onPrimaryTertiaryContainer
import com.mr3y.podcaster.ui.theme.primaryTertiaryContainer
import com.mr3y.podcaster.ui.theme.setStatusBarAppearanceLight
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val NavigationBarHeight: Dp = 80.dp

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
    val isDark = isAppThemeDark()
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

    val strings = LocalStrings.current
    val bottomBarTabs = listOf(
        BottomBarTab(
            strings.tab_subscriptions_label,
            Icons.Outlined.Subscriptions,
            createRoutePattern<Destinations.Subscriptions>(),
            Destinations.Subscriptions,
        ),
        BottomBarTab(
            strings.tab_explore_label,
            Icons.Outlined.Search,
            createRoutePattern<Destinations.Explore>(),
            Destinations.Explore,
        ),
        BottomBarTab(
            strings.tab_settings_label,
            Icons.Outlined.Settings,
            createRoutePattern<Destinations.Settings>(),
            Destinations.Settings,
        ),
    )
    LaunchedEffect(key1 = isPlayerViewExpanded) {
        if (isPlayerViewExpanded) {
            // Save the current status bar appearance to restore it later.
            isStatusBarLightForCurrentScreen = context.isStatusBarAppearanceLight()
            context.setStatusBarAppearanceLight(isAppearanceLight = !isDark)
        } else {
            context.setStatusBarAppearanceLight(isAppearanceLight = isStatusBarLightForCurrentScreen)
        }
    }
    Column(modifier = modifier) {
        var bottomBarYOffset by remember { mutableFloatStateOf(0f) }
        var isBottomBarVisible by remember { mutableStateOf(true) }
        var bottomBarAlpha by remember { mutableFloatStateOf(1f) }
        BoxWithConstraints(modifier = Modifier.weight(1f)) {
            val density = LocalDensity.current
            val expandedPlayerViewHeight = maxHeight
            val collapsedPlayerViewHeight = 104.dp
            val collapsedPlayerViewOffset = with(density) { expandedPlayerViewHeight.toPx() - collapsedPlayerViewHeight.toPx() }
            val navigationBarWindowInsets = NavigationBarDefaults.windowInsets
            val bottomBarHeight = with(density) { NavigationBarHeight.toPx() + navigationBarWindowInsets.getBottom(this) }

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
            LaunchedEffect(currentDestination) {
                if (currentDestination != null && state.currentValue == PlayerViewState.Expanded) {
                    state.animateTo(PlayerViewState.Collapsed)
                }
            }
            LaunchedEffect(state.currentValue) {
                when (state.currentValue) {
                    PlayerViewState.Collapsed -> {
                        if (isPlayerViewExpanded) {
                            appState.collapsePlayerView()
                        }
                    }

                    PlayerViewState.Expanded -> {
                        isBottomBarVisible = false
                        if (!isPlayerViewExpanded) {
                            appState.expandPlayerView()
                        }
                    }
                }
            }
            LaunchedEffect(Unit) {
                snapshotFlow { state.offset }
                    .filter { !it.isNaN() }
                    .map { it / collapsedPlayerViewOffset }
                    .collect { fraction ->
                        bottomBarYOffset = (1f - fraction) * bottomBarHeight
                        bottomBarAlpha = fraction.coerceAtLeast(0.5f)
                    }
            }
            LaunchedEffect(key1 = state.targetValue) {
                if (state.targetValue == PlayerViewState.Collapsed) {
                    isBottomBarVisible = true
                }
            }
            PodcasterNavGraph(
                navController = navController,
                onNavDrawerClick = {},
                appState = appState,
                userPreferences = userPreferences,
                contentPadding = PaddingValues(bottom = if (currentlyPlayingEpisode != null) collapsedPlayerViewHeight else 0.dp),
                excludedWindowInsets = navigationBarWindowInsets,
                modifier = Modifier.fillMaxWidth()
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
                            containerColor = containerColor,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .padding(bottom = 4.dp)
                        )
                    } else {
                        ExpandedPlayerView(
                            currentlyPlayingEpisode = activeEpisode,
                            onResume = appState::resume,
                            onPause = appState::pause,
                            isSeekingToPreviousEnabled = appState.canSeekToPreviousInQueue,
                            onSeekToPrevious = appState::seekToPreviousInQueue,
                            isSeekingToNextEnabled = appState.canSeekToNextInQueue,
                            onSeekToNext = appState::seekToNextInQueue,
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
        if (isBottomBarVisible) {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        val height = (placeable.height - bottomBarYOffset.roundToInt()).coerceAtLeast(0)
                        layout(placeable.width, height) {
                            placeable.placeRelative(0, 0)
                        }
                    }
                    .graphicsLayer {
                        alpha = bottomBarAlpha
                    }
            ) {
                bottomBarTabs.forEach { tab ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                    val tabScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 1f,
                        label = "AnimatedTabScale"
                    )
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
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
                        label = { Text(text = tab.label) },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryTertiaryContainer,
                            indicatorColor = MaterialTheme.colorScheme.primaryTertiaryContainer
                        ),
                        modifier = Modifier.scale(tabScale)
                    )
                }
            }
        }
    }
}

data class BottomBarTab(
    val label: String,
    val icon: ImageVector,
    val route: String,
    val destination: Destinations,
)

private enum class PlayerViewState {
    Expanded,
    Collapsed,
}
