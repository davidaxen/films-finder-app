package com.darvi.filmhunter.presentation.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.darvi.filmhunter.presentation.auth.login.LoginScreen
import com.darvi.filmhunter.presentation.auth.register.RegisterScreen
import com.darvi.filmhunter.presentation.core.navigation.bottomnav.favGraph
import com.darvi.filmhunter.presentation.core.navigation.bottomnav.homeGraph
import com.darvi.filmhunter.presentation.core.navigation.bottomnav.profileGraph

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
    navigation<AppGraph.Main>(startDestination = MainGraph.Home) {
        homeGraph(navController)
        favGraph(navController)
        profileGraph(navController)
    }
}