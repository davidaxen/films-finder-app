package com.darvi.filmhunter.presentation.core.navigation.bottomnav

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationItemIconPosition
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.ShortNavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import kotlinx.collections.immutable.persistentListOf

@Composable
fun BottomBar(
    currentDestination: NavDestination?,
    onNavigate: (Any) -> Unit
) {
    val tabs = remember {
        listOf(
            BottomBarDestination.Home,
            BottomBarDestination.Fav,
            BottomBarDestination.Profile,
        )
    }
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        /*Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .shimmerLoading()
        )*/
        tabs.forEach { tab ->
            val selected =
                currentDestination?.hierarchy?.any { it.hasRoute(tab.route::class) } == true
            ShortNavigationBarItem(
                selected = selected,
                onClick = { onNavigate(tab.route) },
                icon = if (selected) tab.selectedIcon else tab.unselectedIcon,
                label = {
                    Text(
                        tab.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp)
                    )
                },
                iconPosition = NavigationItemIconPosition.Start,
                colors = ShortNavigationBarItemDefaults.colors(
                    selectedIndicatorColor = MaterialTheme.colorScheme.primary,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun Modifier.shimmerLoading(
    durationMillis: Int = 1000,
): Modifier {
    val transition = rememberInfiniteTransition(label = "")

    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "",
    )

    return drawBehind {
        drawRect(
            brush = Brush.linearGradient(
                colors = persistentListOf(
                    Color.LightGray.copy(alpha = 0.2f),
                    Color.LightGray.copy(alpha = 1.0f),
                    Color.LightGray.copy(alpha = 0.2f),
                ),
                start = Offset(x = translateAnimation, y = translateAnimation),
                end = Offset(x = translateAnimation + 100f, y = translateAnimation + 100f),
            )
        )
    }
}