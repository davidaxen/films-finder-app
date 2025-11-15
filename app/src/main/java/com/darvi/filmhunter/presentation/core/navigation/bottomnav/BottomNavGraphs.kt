package com.darvi.filmhunter.presentation.core.navigation.bottomnav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.darvi.filmhunter.presentation.core.navigation.FavRoutes
import com.darvi.filmhunter.presentation.core.navigation.HomeRoutes
import com.darvi.filmhunter.presentation.core.navigation.MainGraph
import com.darvi.filmhunter.presentation.core.navigation.ProfileRoutes
import com.darvi.filmhunter.presentation.core.navigation.SearchRoutes
import com.darvi.filmhunter.presentation.list.movie.MovieListScreen
import com.darvi.filmhunter.presentation.list.series.SeriesListScreen
import com.darvi.filmhunter.presentation.search.QueryListScreen
import com.darvi.filmhunter.presentation.search.SearchScreen

fun NavGraphBuilder.homeGraph(navController: NavController) {
    navigation<MainGraph.Home>(startDestination = HomeRoutes.MoviesList) {
        composable<HomeRoutes.MoviesList> {
            MovieListScreen()
        }
        composable<HomeRoutes.SeriesList> {
            SeriesListScreen()
        }
    }
}

fun NavGraphBuilder.searchGraph(navController: NavController) {
    navigation<MainGraph.Search>(startDestination = SearchRoutes.Main) {
        composable<SearchRoutes.Main> {
            SearchScreen(
                onSeeAllClick = { q, type ->
                    navController.navigate(
                        SearchRoutes.QueryList(
                            query = q,
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
                onBackPress = { navController.popBackStack() }
            )
        }
    }
}

fun NavGraphBuilder.favGraph(navController: NavController) {
    navigation<MainGraph.Fav>(startDestination = FavRoutes.Main) {
        composable<FavRoutes.Main> {
        }
    }
}

fun NavGraphBuilder.profileGraph(navController: NavController) {
    navigation<MainGraph.Profile>(startDestination = ProfileRoutes.Main) {
        composable<ProfileRoutes.Main> {
        }
    }
}