package com.darvi.filmhunter.presentation.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun NavigationWrapper(
//    sessionViewModel: SessionViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
//    val user by sessionViewModel.currentUser.collectAsStateWithLifecycle(null)

//    LaunchedEffect(user) {
//        Log.i("PRUEBA SESSION NAV", user.toString())
//    }

    NavHost(navController = navController, startDestination = AppGraph.Main) {
        authGraph(navController)
        mainGraph(navController)
    }
}