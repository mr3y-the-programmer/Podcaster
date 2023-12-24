package com.mr3y.podcaster.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
@Composable
fun HomeScreen(
    appState: PodcasterAppState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val currentlyPlayingEpisode by appState.currentlyPlayingEpisode.collectAsStateWithLifecycle()
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
            Icons.Outlined.Home,
            createRoutePattern<Destinations.Subscriptions>(),
            Destinations.Subscriptions
        ),
        DrawerTab(
            strings.tab_explore_label,
            Icons.Outlined.Search,
            createRoutePattern<Destinations.Explore>(),
            Destinations.Explore
        ),
        DrawerTab(
            strings.tab_downloads_label,
            Icons.Outlined.FileDownload,
            createRoutePattern<Destinations.Downloads>(),
            Destinations.Downloads
        )
    )
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
                        icon = { Icon(imageVector = tab.icon, contentDescription = null) },
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
                        }
                    )
                }
            }
        },
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            val collapsedPlayerViewHeight = 104.dp
            val playerViewBottomInsets = WindowInsets.navigationBars
            PodcasterNavGraph(
                navController = navController,
                onNavDrawerClick = {
                    scope.launch { drawerState.open() }
                },
                appState = appState,
                contentPadding = PaddingValues(bottom = if (currentlyPlayingEpisode != null) collapsedPlayerViewHeight else 0.dp),
                excludedWindowInsets = if (currentlyPlayingEpisode != null) playerViewBottomInsets else null
            )
            if (currentlyPlayingEpisode != null) {
                AnimatedContent(
                    targetState = currentlyPlayingEpisode,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                                slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Up, initialOffset = { -it }))
                            .togetherWith(
                                fadeOut(animationSpec = tween(90)) + slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Down)
                            )
                    },
                    contentKey = { null },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .height(collapsedPlayerViewHeight),
                    label = "Animated PlayerView"
                ) { targetState ->
                    if (targetState != null) {
                        CollapsedPlayerView(
                            currentlyPlayingEpisode = targetState,
                            onResume = appState::resume,
                            onPause = appState::pause,
                            contentWindowInsets = playerViewBottomInsets
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
    val destination: Destinations
)
