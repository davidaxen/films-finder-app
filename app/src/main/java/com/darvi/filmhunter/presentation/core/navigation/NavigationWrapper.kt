package com.darvi.filmhunter.presentation.core.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.navigation.bottomnav.BottomBar
import com.darvi.filmhunter.presentation.list.model.FilmType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    var selectedTabRow by rememberSaveable { mutableStateOf(FilmType.MOVIE) }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (destination?.hierarchy?.any { it.hasRoute<MainGraph.Home>() } == true) {
                PrimaryTabRow(
                    selectedTabIndex = selectedTabRow.value,
                    containerColor = MaterialTheme.colorScheme.background,
                ) {
                    Tab(
                        selected = selectedTabRow == FilmType.MOVIE,
                        onClick = {
                            selectedTabRow = FilmType.MOVIE
                            navController.navigate(HomeRoutes.MoviesList)
                        },
                        modifier = Modifier.height(40.dp)
                    ) {
                        FilmHunterText(text = "Películas")
                    }
                    Tab(
                        selected = selectedTabRow == FilmType.SERIES,
                        onClick = {
                            selectedTabRow = FilmType.SERIES
                            navController.navigate(HomeRoutes.SeriesList)
                        },
                        modifier = Modifier.height(40.dp)
                    ) {
                        FilmHunterText(text = "Series")
                    }
                }
            }

            NavHost(
                navController = navController,
                startDestination = AppGraph.Main,
            ) {
                authGraph(navController)
                mainGraph(navController)
            }
        }
    }
}