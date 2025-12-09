package com.darvi.filmhunter.presentation.core.navigation.bottomnav

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
        composable<MatchRoutes.Main> {
            MatcherScreen(
                onJoinRoomClick = { code ->
//                    navController.navigate(
//                        MainGraph.Room(code = code)
//                    )
                }
            )
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