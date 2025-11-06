package com.darvi.filmhunter.presentation.core.navigation.bottomnav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.darvi.filmhunter.presentation.core.navigation.MainGraph
import kotlinx.serialization.Serializable

@Serializable
sealed class BottomBarDestination<T>(
    var title: String,
    val selectedIcon: @Composable () -> Unit,
    val unselectedIcon: @Composable () -> Unit,
    val route: T
) {
    @Serializable
    data object Home: BottomBarDestination<MainGraph.Home>(
        title = "Lista",
        selectedIcon = {
            Icon(imageVector = Icons.AutoMirrored.Filled.ViewList, contentDescription = "")
        },
        unselectedIcon = {
            Icon(imageVector = Icons.AutoMirrored.Outlined.ViewList, contentDescription = "")
        },
        route = MainGraph.Home
    )

    @Serializable
    data object Fav: BottomBarDestination<MainGraph.Fav>(
        title = "Guardado",
        selectedIcon = {
            Icon(imageVector = Icons.Default.Bookmark, contentDescription = "")
        },
        unselectedIcon = {
            Icon(imageVector = Icons.Default.Bookmark, contentDescription = "")
        },
        route = MainGraph.Fav
    )

    @Serializable
    data object Profile: BottomBarDestination<MainGraph.Profile>(
        title = "Perfil",
        selectedIcon = {
            Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "")
        },
        unselectedIcon = {
            Icon(imageVector = Icons.Outlined.AccountCircle, contentDescription = "")
        },
        route = MainGraph.Profile
    )
}