package com.darvi.filmhunter.presentation.core.navigation.bottomnav

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy

@Composable
fun BottomBar(
    currentDestination: NavDestination?,
    onNavigate: (Any) -> Unit
) {
    val tabs = remember {
        listOf(
            BottomBarDestination.Home,
            BottomBarDestination.Search,
            BottomBarDestination.Fav,
            BottomBarDestination.Profile,
        )
    }
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        tabs.forEach { tab ->
            val selected =
                currentDestination?.hierarchy?.any { it.hasRoute(tab.route::class) } == true
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(tab.route) },
                icon = if (selected) tab.selectedIcon else tab.unselectedIcon,
                label = {
                    Text(
                        tab.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
