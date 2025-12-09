package com.darvi.filmhunter.presentation.core.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.darvi.filmhunter.domain.entity.SessionState
import com.darvi.filmhunter.presentation.core.SessionViewModel
import com.darvi.filmhunter.presentation.core.components.FilmHunterText
import com.darvi.filmhunter.presentation.core.model.FilmType
import com.darvi.filmhunter.presentation.core.navigation.bottomnav.BottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationWrapper(
    sessionViewModel: SessionViewModel,
) {
    val userSessionState by sessionViewModel.sessionState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    var selectedTabRow by rememberSaveable { mutableStateOf(FilmType.MOVIE) }

    val showBottomBar = destination?.hierarchy?.any {
        it.hasRoute<HomeRoutes.MoviesList>() ||
        it.hasRoute<HomeRoutes.SeriesList>() ||
        it.hasRoute<SearchRoutes.Main>() ||

                
//        it.hasRoute<MainGraph.Home>() ||
//        it.hasRoute<MainGraph.Search>() ||
        it.hasRoute<MainGraph.Saved>() ||
        it.hasRoute<MainGraph.Profile>()
    } == true

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
            ) {
                BottomBar(destination) {
                    navController.navigate(it) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    }
                }
            }
//            if (showBottomBar) BottomBar(destination) {
//            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedVisibility(
                visible = destination?.hierarchy?.any { it.hasRoute<MainGraph.Home>() } == true,
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut() + slideOutVertically { -it },
            ) {

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

            when (val state = userSessionState) {
                SessionState.Loading -> {
                    // El splash de Android 12 sigue tapando esto gracias a setKeepOnScreenCondition.
                    // Si quieres, puedes poner un loader interno si el splash ya no está:
                    // Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                }
                is SessionState.Authenticated, SessionState.Unauthenticated -> {
                    NavHost(
                        navController = navController,
                        startDestination = if (state is SessionState.Authenticated)
                                        AppGraph.Main
                                    else
                                        AppGraph.Auth,
                    ) {
                        authGraph(navController)
                        mainGraph(navController)
                    }
                }
            }
        }
    }
}