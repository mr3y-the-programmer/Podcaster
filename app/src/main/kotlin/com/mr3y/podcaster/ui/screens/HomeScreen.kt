package com.mr3y.podcaster.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.zIndex
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kiwi.navigationcompose.typed.createRoutePattern
import com.kiwi.navigationcompose.typed.navigate
import com.mr3y.podcaster.ui.navigation.Destinations
import com.mr3y.podcaster.ui.navigation.PodcasterNavGraph
import kotlinx.serialization.ExperimentalSerializationApi

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    Box(
        modifier = modifier
    ) {
        PodcasterNavGraph(
            navController = navController,
            modifier = Modifier.zIndex(1f)
        )
        BottomBar(
            navController = navController,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .zIndex(2f)
        )
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Composable
fun BottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomBarTabs = listOf(
        BottomBarTab("Subscriptions", Icons.Outlined.Home, createRoutePattern<Destinations.Subscriptions>(), Destinations.Subscriptions),
        BottomBarTab("Explore", Icons.Outlined.Search, createRoutePattern<Destinations.Explore>(), Destinations.Explore)
    )

    NavigationBar(
        modifier = modifier
    ) {
        bottomBarTabs.forEach { tab ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                onClick = {
                    navController.navigate(tab.destination) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                label = { Text(text = tab.label) },
                icon = { Icon(imageVector = tab.icon, contentDescription = null) }
            )
        }
    }
}

data class BottomBarTab(
    val label: String,
    val icon: ImageVector,
    val route: String,
    val destination: Destinations
)
