package com.darvi.filmhunter.presentation.core.navigation.bottomnav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.darvi.filmhunter.presentation.core.navigation.FavRoutes
import com.darvi.filmhunter.presentation.core.navigation.HomeRoutes
import com.darvi.filmhunter.presentation.core.navigation.MainGraph
import com.darvi.filmhunter.presentation.core.navigation.ProfileRoutes
import com.darvi.filmhunter.presentation.list.ListScreen

fun NavGraphBuilder.homeGraph(navController: NavController) {
    navigation<MainGraph.Home>(startDestination = HomeRoutes.HomeList) {
        composable<HomeRoutes.HomeList> {
            ListScreen()
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