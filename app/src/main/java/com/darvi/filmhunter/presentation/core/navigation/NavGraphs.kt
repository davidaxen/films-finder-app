package com.darvi.filmhunter.presentation.core.navigation

import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.darvi.filmhunter.presentation.auth.login.LoginScreen
import com.darvi.filmhunter.presentation.auth.register.RegisterScreen
import com.darvi.filmhunter.presentation.core.navigation.bottomnav.savedGraph
import com.darvi.filmhunter.presentation.core.navigation.bottomnav.homeGraph
import com.darvi.filmhunter.presentation.core.navigation.bottomnav.profileGraph
import com.darvi.filmhunter.presentation.core.navigation.bottomnav.searchGraph
import com.darvi.filmhunter.presentation.detail.DetailScreen
import com.darvi.filmhunter.presentation.detail.SeasonDetailScreen

fun NavGraphBuilder.authGraph(navController: NavController) {
    navigation<AppGraph.Auth>(startDestination = AuthRoutes.Login) {
        composable<AuthRoutes.Login> {
            LoginScreen(
                navigateToMain = dropUnlessResumed {
                    navController.navigate(AppGraph.Main) {
                        popUpTo(AppGraph.Auth) { inclusive = true }
                        launchSingleTop = true
                    }
                 },
                navigateToRegister = dropUnlessResumed { navController.navigate(AuthRoutes.Register) },
                navigateToForgotPassword = dropUnlessResumed {}
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
    navigation<AppGraph.Main>(startDestination = MainGraph.Search) {
        homeGraph(navController)
        searchGraph(navController)
        savedGraph(navController)
        profileGraph(navController)
        composable<MainGraph.Detail> { stackEntry ->
            val data = stackEntry.toRoute<MainGraph.Detail>()
            DetailScreen(
                filmId = data.id,
                filmType = data.filmType,
                onSeasonClick = { seriesId, seasonNumber ->
                    navController.navigate(
                        MainGraph.SeasonDetail(seriesId = seriesId, seasonNumber = seasonNumber)
                    )
                },
                onFilmRecommendedClick =  { id, type ->
                    navController.navigate(
                        MainGraph.Detail(id = id, filmType = type)
                    )
                },
                onGenreClick = { genre, type ->
                    navController.navigate(
                        SearchRoutes.FilmsGenreList(
                            genre = genre,
                            filmType = type
                        )
                    )
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable<MainGraph.SeasonDetail> { stackEntry ->
            val data = stackEntry.toRoute<MainGraph.SeasonDetail>()
            SeasonDetailScreen(
                seriesId = data.seriesId,
                seasonNumber = data.seasonNumber,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}