package com.darvi.filmhunter.presentation.core.navigation.bottomnav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.darvi.filmhunter.presentation.core.navigation.FavRoutes
import com.darvi.filmhunter.presentation.core.navigation.HomeRoutes
import com.darvi.filmhunter.presentation.core.navigation.MainGraph
import com.darvi.filmhunter.presentation.core.navigation.ProfileRoutes
import com.darvi.filmhunter.presentation.list.movie.MovieListScreen

fun NavGraphBuilder.homeGraph(navController: NavController) {
    navigation<MainGraph.Home>(startDestination = HomeRoutes.MoviesList) {
        composable<HomeRoutes.MoviesList> {
            MovieListScreen()
        }
        composable<HomeRoutes.SeriesList> {

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