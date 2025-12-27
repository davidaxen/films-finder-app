package com.darvi.filmhunter.presentation.core.navigation.bottomnav

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.darvi.filmhunter.presentation.core.navigation.HomeRoutes
import com.darvi.filmhunter.presentation.core.navigation.MainGraph
import com.darvi.filmhunter.presentation.core.navigation.MatchRoutes
import com.darvi.filmhunter.presentation.core.navigation.ProfileRoutes
import com.darvi.filmhunter.presentation.core.navigation.SavedRoutes
import com.darvi.filmhunter.presentation.core.navigation.SearchRoutes
import com.darvi.filmhunter.presentation.list.movie.MovieListScreen
import com.darvi.filmhunter.presentation.list.series.SeriesListScreen
import com.darvi.filmhunter.presentation.matcher.MatcherScreen
import com.darvi.filmhunter.presentation.matcher.MatcherViewModel
import com.darvi.filmhunter.presentation.matcher.SessionCreationState
import com.darvi.filmhunter.presentation.matcher.screens.FilmTypeSelectionScreen
import com.darvi.filmhunter.presentation.matcher.screens.GenreSelectionScreen
import com.darvi.filmhunter.presentation.matcher.screens.MatcherSummaryScreen
import com.darvi.filmhunter.presentation.matcher.screens.PlatformSelectionScreen
import com.darvi.filmhunter.presentation.matcher.screens.SessionWaitingScreen
import com.darvi.filmhunter.presentation.matcher.screens.SwipingScreen
import com.darvi.filmhunter.presentation.saved.SavedListScreen
import com.darvi.filmhunter.presentation.search.FilmsByGenreListScreen
import com.darvi.filmhunter.presentation.search.HomeFilmsListScreen
import com.darvi.filmhunter.presentation.search.QueryListScreen
import com.darvi.filmhunter.presentation.search.SearchScreen

fun NavGraphBuilder.homeGraph(navController: NavController) {
    navigation<MainGraph.Home>(startDestination = HomeRoutes.MoviesList) {
        composable<HomeRoutes.MoviesList> {
            MovieListScreen(
                onSeeAllClick = { searchMethod, type ->
                    navController.navigate(
                        SearchRoutes.HomeFilmsList(
                            searchMethod = searchMethod,
                            filmType = type
                        )
                    )
                },
                onFilmClick = { id, type ->
                    navController.navigate(
                        MainGraph.Detail(id = id, filmType = type)
                    )
                }
            )
        }
        composable<HomeRoutes.SeriesList> {
            SeriesListScreen(
                onSeeAllClick = { searchMethod, type ->
                    navController.navigate(
                        SearchRoutes.HomeFilmsList(
                            searchMethod = searchMethod,
                            filmType = type
                        )
                    )
                },
                onFilmClick = { id, type ->
                    navController.navigate(
                        MainGraph.Detail(id = id, filmType = type)
                    )
                }
            )
        }
    }
}

fun NavGraphBuilder.searchGraph(navController: NavController) {
    navigation<MainGraph.Search>(startDestination = SearchRoutes.Main) {
        composable<SearchRoutes.Main> {
            SearchScreen(
                onFilmClick = { id, type ->
                    navController.navigate(
                        MainGraph.Detail(id = id, filmType = type)
                    )
                },
                onSeeAllClick = { q, type ->
                    navController.navigate(
                        SearchRoutes.QueryList(
                            query = q,
                            filmType = type
                        )
                    )
                },
                onSearchByGenres = { genre, platformId, type ->
                    navController.navigate(
                        SearchRoutes.FilmsGenreList(
                            genre = genre,
                            platformId = platformId,
                            filmType = type
                        )
                    )
                }
            )
        }
        composable<SearchRoutes.QueryList> { stackEntry ->
            val data = stackEntry.toRoute<SearchRoutes.QueryList>()
            QueryListScreen(
                query = data.query,
                filmType = data.filmType,
                onFilmClick = { id, type ->
                    navController.navigate(
                        MainGraph.Detail(id = id, filmType = type)
                    )
                },
                onBackPress = { navController.popBackStack() }
            )
        }
        composable<SearchRoutes.FilmsGenreList> { stackEntry ->
            val data = stackEntry.toRoute<SearchRoutes.FilmsGenreList>()
            FilmsByGenreListScreen(
                genre = data.genre,
                filmType = data.filmType,
                platformId = data.platformId,
                onFilmClick = { id, type ->
                    navController.navigate(
                        MainGraph.Detail(id = id, filmType = type)
                    )
                },
                onBackPress = { navController.popBackStack() }
            )
        }
        composable<SearchRoutes.HomeFilmsList> { stackEntry ->
            val data = stackEntry.toRoute<SearchRoutes.HomeFilmsList>()
            HomeFilmsListScreen(
                searchMethod = data.searchMethod,
                filmType = data.filmType,
                onFilmClick = { id, type ->
                    navController.navigate(
                        MainGraph.Detail(id = id, filmType = type)
                    )
                },
                onBackPress = { navController.popBackStack() }
            )
        }
    }
}

fun NavGraphBuilder.matchGraph(navController: NavController) {
    navigation<MainGraph.Match>(startDestination = MatchRoutes.Main) {
        composable<MatchRoutes.Main> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(MainGraph.Match)
            }
            val sharedViewModel: MatcherViewModel = hiltViewModel(parentEntry)
            MatcherScreen(
                onJoinRoomClick = { code ->
                    sharedViewModel.joinSession(
                        code = code,
                        onSuccess = { session ->
                            navController.navigate(
                                MatchRoutes.SessionWaiting(
                                    sessionCode = session.code,
                                    sessionId = session.id
                                )
                            )
                        },
                        onError = { error ->

                        }
                    )
                },
                onCreateRoomClick = {
                    navController.navigate(MatchRoutes.FilmTypeSelection)
                }
            )
        }
        composable<MatchRoutes.FilmTypeSelection>(
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = tween(300)
                )
            }
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(MainGraph.Match)
            }
            val sharedViewModel: MatcherViewModel = hiltViewModel(parentEntry)
            FilmTypeSelectionScreen(
                matcherViewModel = sharedViewModel,
                onFilmTypeSelected = {
                    navController.navigate(MatchRoutes.GenreSelection)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable<MatchRoutes.GenreSelection> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(MainGraph.Match)
            }
            val sharedViewModel: MatcherViewModel = hiltViewModel(parentEntry)
            GenreSelectionScreen(
                matcherViewModel = sharedViewModel,
                onGenresSelected = {
                    navController.navigate(MatchRoutes.PlatformSelection)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable<MatchRoutes.PlatformSelection> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(MainGraph.Match)
            }
            val sharedViewModel: MatcherViewModel = hiltViewModel(parentEntry)
            PlatformSelectionScreen(
                matcherViewModel = sharedViewModel,
                onPlatformsSelected = {
                    navController.navigate(MatchRoutes.Summary)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable<MatchRoutes.Summary> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(MainGraph.Match)
            }
            val sharedViewModel: MatcherViewModel = hiltViewModel(parentEntry)
            MatcherSummaryScreen(
                matcherViewModel = sharedViewModel,
                onCreateSession = { sessionCode ->
                    // Get session ID from the created session
                    val sessionState = sharedViewModel.sessionCreationState.value
                    if (sessionState is SessionCreationState.Success) {
                        navController.navigate(
                            MatchRoutes.SessionWaiting(
                                sessionCode = sessionCode,
                                sessionId = sessionState.session.id
                            )
                        )
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable<MatchRoutes.SessionWaiting>(
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideInVertically(
                    initialOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(300)
                )
            }
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(MainGraph.Match)
            }
            val sharedViewModel: MatcherViewModel = hiltViewModel(parentEntry)
            val route = backStackEntry.toRoute<MatchRoutes.SessionWaiting>()
            val currentUser by sharedViewModel.currentUser.collectAsStateWithLifecycle()
            val currentSession by sharedViewModel.currentSession.collectAsStateWithLifecycle()
            val hasOtherUserJoined by sharedViewModel.hasOtherUserJoined.collectAsStateWithLifecycle()
            val sessionBecameActive by sharedViewModel.sessionBecameActive.collectAsStateWithLifecycle()
            
            // Determine if current user is the host
            val isHost = remember(route.sessionId, currentUser, currentSession) {
                currentSession?.let { session ->
                    session.id == route.sessionId && session.createdBy == currentUser?.id
                } ?: false
            }
            
            // Navigate to SwipingScreen when session becomes active
            androidx.compose.runtime.LaunchedEffect(sessionBecameActive) {
                if (sessionBecameActive) {
                    navController.navigate(
                        MatchRoutes.Swiping(sessionId = route.sessionId)
                    ) {
                        // Clear the back stack up to Main
                        popUpTo(MatchRoutes.Main) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            }
            
            SessionWaitingScreen(
                sessionCode = route.sessionCode,
                isHost = isHost,
                hasOtherUserJoined = hasOtherUserJoined,
                onCancelSession = if (isHost) {
                    {
                        sharedViewModel.cancelSession(
                            sessionId = route.sessionId,
                            onSuccess = {
                                navController.navigate(MatchRoutes.Main) {
                                    popUpTo(MatchRoutes.Main) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            },
                            onError = { error ->
                                // TODO: Show error message
                            }
                        )
                    }
                } else null,
                onInitiateSession = if (isHost && hasOtherUserJoined) {
                    {
                        sharedViewModel.initiateSession(
                            sessionId = route.sessionId,
                            onSuccess = {
                                // Navigation will happen automatically via LaunchedEffect when sessionBecameActive becomes true
                            },
                            onError = { error ->
                                // TODO: Show error message
                            }
                        )
                    }
                } else null,
            )
        }
        composable<MatchRoutes.Swiping>(
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideInVertically(
                    initialOffsetY = { fullHeight -> -fullHeight },
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(300)
                )
            }
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<MatchRoutes.Swiping>()
            SwipingScreen(sessionId = route.sessionId)
        }
    }
}

fun NavGraphBuilder.savedGraph(navController: NavController) {
    navigation<MainGraph.Saved>(startDestination = SavedRoutes.Main) {
        composable<SavedRoutes.Main> {
            SavedListScreen(
                onFilmClick = { id, type ->
                    navController.navigate(
                        MainGraph.Detail(id = id, filmType = type)
                    )
                }
            )
        }
    }
}

fun NavGraphBuilder.profileGraph(navController: NavController) {
    navigation<MainGraph.Profile>(startDestination = ProfileRoutes.Main) {
        composable<ProfileRoutes.Main> {
        }
    }
}