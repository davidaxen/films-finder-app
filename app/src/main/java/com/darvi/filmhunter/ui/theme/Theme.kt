package com.darvi.filmhunter.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = RedPrimary,          // Color principal: botones primarios, iconos activos, barras destacadas
    secondary = RedSecondary,      // Color secundario: botones o acentos secundarios, chips, sliders, etc.
    background = DarkBackground,   // Fondo general de la app (pantallas, scaffolds)
    surface = DarkSurface,         // Fondos de tarjetas, diálogos, barras inferiores/superiores
    onPrimary = DarkOnPrimary,     // Texto/iconos sobre elementos con color "primary" (debe tener alto contraste)
    onBackground = DarkOnBackground, // Texto principal sobre el fondo general
    onSurface = DarkOnSurface,     // Texto o iconos secundarios sobre superficies (menos contraste que onBackground)
    error = DarkError              // Indicadores de error: textos de validación, iconos o bordes de campos erróneos
)

//private val DarkColorScheme = darkColorScheme(
//    primary = DarkPrimary,
//    onPrimary = DarkOnPrimary,
//    primaryContainer = DarkPrimaryContainer,
//    onPrimaryContainer = DarkOnPrimaryContainer,
//    background = DarkBackground,
//    surface = DarkSurface,
//    onBackground = DarkOnBackground,
//    onSurface = DarkOnBackground,
//    onSurfaceVariant = DarkOnSurfaceVariant,
//    error = DarkError
//)

//private val LightColorScheme = lightColorScheme(
//    primary = LightPrimary,
//    onPrimary = LightOnPrimary,
//    primaryContainer = LightPrimaryContainer,
//    onPrimaryContainer = LightOnPrimaryContainer,
//    background = LightBackground,
//    surface = LightSurface,
//    onBackground = LightOnBackground,
//    onSurface = LightOnBackground,
//    onSurfaceVariant = LightOnSurfaceVariant,
//    error = LightError
//)

@Composable
fun FilmHunterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}