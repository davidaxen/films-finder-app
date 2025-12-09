package com.darvi.filmhunter.presentation.core.navigation.bottomnav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
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
        title = "Inicio",
        selectedIcon = {
            Icon(imageVector = Icons.Filled.Home, contentDescription = "")
        },
        unselectedIcon = {
            Icon(imageVector = Icons.Outlined.Home, contentDescription = "")
        },
        route = MainGraph.Home
    )

    @Serializable
    data object Search: BottomBarDestination<MainGraph.Search>(
        title = "Buscar",
        selectedIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "")
        },
        unselectedIcon = {
            Icon(imageVector = Icons.Outlined.Search, contentDescription = "")
        },
        route = MainGraph.Search
    )

    @Serializable
    data object Match: BottomBarDestination<MainGraph.Match>(
        title = "Match",
        selectedIcon = {
            Icon(imageVector = Icons.Filled.Group, contentDescription = "")
        },
        unselectedIcon = {
            Icon(imageVector = Icons.Outlined.Group, contentDescription = "")
        },
        route = MainGraph.Match
    )

    @Serializable
    data object Fav: BottomBarDestination<MainGraph.Saved>(
        title = "Guardado",
        selectedIcon = {
            Icon(imageVector = Icons.Default.Bookmark, contentDescription = "")
        },
        unselectedIcon = {
            Icon(imageVector = Icons.Default.Bookmark, contentDescription = "")
        },
        route = MainGraph.Saved
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