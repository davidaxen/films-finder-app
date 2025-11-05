package com.darvi.filmhunter.presentation.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.darvi.filmhunter.presentation.core.navigation.bottomnav.BottomBar

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val destination = navController.currentBackStackEntryAsState().value?.destination

    val showBottomBar = destination?.hierarchy?.any {
        it.hasRoute<MainGraph.Home>() || it.hasRoute<MainGraph.Fav>() || it.hasRoute<MainGraph.Profile>()
    } == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) BottomBar(destination) {
                navController.navigate(it) {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = AppGraph.Main,
            modifier = Modifier.padding(padding)
        ) {
            authGraph(navController)
            mainGraph(navController)
        }
    }
}