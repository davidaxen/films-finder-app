package com.darvi.filmhunter.presentation.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darvi.filmhunter.presentation.auth.register.RegisterScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Register) {
        composable<Login> {
        }

        composable<Register> {
            RegisterScreen()
        }
    }
}