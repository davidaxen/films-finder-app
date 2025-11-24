package com.darvi.filmhunter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.darvi.filmhunter.presentation.core.SessionState
import com.darvi.filmhunter.presentation.core.SessionViewModel
import com.darvi.filmhunter.presentation.core.navigation.NavigationWrapper
import com.darvi.filmhunter.ui.theme.FilmHunterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val sessionViewModel: SessionViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition {
            sessionViewModel.sessionState.value is SessionState.Loading
        }

        setContent {
            FilmHunterTheme {
                NavigationWrapper(
                    sessionViewModel = sessionViewModel,
                )
            }
        }
    }
}