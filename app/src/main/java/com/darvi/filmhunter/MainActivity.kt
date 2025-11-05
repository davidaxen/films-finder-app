package com.darvi.filmhunter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.darvi.filmhunter.presentation.core.navigation.NavigationWrapper
import com.darvi.filmhunter.ui.theme.FilmHunterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FilmHunterTheme {
                NavigationWrapper()
            }
        }
    }
}