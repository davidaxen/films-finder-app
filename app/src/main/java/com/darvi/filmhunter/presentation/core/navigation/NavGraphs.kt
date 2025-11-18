package com.darvi.filmhunter.presentation.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.darvi.filmhunter.presentation.auth.login.LoginScreen
import com.darvi.filmhunter.presentation.auth.register.RegisterScreen
import com.darvi.filmhunter.presentation.core.navigation.bottomnav.favGraph
import com.darvi.filmhunter.presentation.core.navigation.bottomnav.homeGraph
import com.darvi.filmhunter.presentation.core.navigation.bottomnav.profileGraph
import com.darvi.filmhunter.presentation.core.navigation.bottomnav.searchGraph
import com.darvi.filmhunter.presentation.detail.DetailScreen

fun NavGraphBuilder.authGraph(navController: NavController) {
    navigation<AppGraph.Auth>(startDestination = AuthRoutes.Login) {
        composable<AuthRoutes.Login> {
            LoginScreen(
                navigateToRegister = { navController.navigate(AuthRoutes.Register) },
                navigateToForgotPassword = {}
            )
        }

        composable<AuthRoutes.Register> {
            RegisterScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}

fun NavGraphBuilder.mainGraph(navController: NavController) {
//    navigation<AppGraph.Main>(startDestination = MainGraph.Home) {
    navigation<AppGraph.Main>(startDestination = MainGraph.Search) {
        homeGraph(navController)
        searchGraph(navController)
        favGraph(navController)
        profileGraph(navController)
        composable<MainGraph.Detail> { stackEntry ->
            val data = stackEntry.toRoute<MainGraph.Detail>()
            DetailScreen(
                filmId = data.id,
                filmType = data.filmType,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}