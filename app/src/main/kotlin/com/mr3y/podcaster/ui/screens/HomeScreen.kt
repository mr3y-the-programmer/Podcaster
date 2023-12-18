package com.mr3y.podcaster.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kiwi.navigationcompose.typed.createRoutePattern
import com.kiwi.navigationcompose.typed.navigate
import com.mr3y.podcaster.LocalStrings
import com.mr3y.podcaster.ui.navigation.Destinations
import com.mr3y.podcaster.ui.navigation.PodcasterNavGraph
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

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
                    NavigationDrawerItem(
                        label = { Text(text = tab.label) },
                        icon = { Icon(imageVector = tab.icon, contentDescription = null) },
                        selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(tab.destination) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        modifier = modifier
    ) {
        PodcasterNavGraph(
            navController = navController,
            onNavDrawerClick = {
                scope.launch { drawerState.open() }
            }
        )
    }
}

data class DrawerTab(
    val label: String,
    val icon: ImageVector,
    val route: String,
    val destination: Destinations
)
