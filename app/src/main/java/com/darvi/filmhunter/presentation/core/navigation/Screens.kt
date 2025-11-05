package com.darvi.filmhunter.presentation.core.navigation

import kotlinx.serialization.Serializable

interface AppGraph {
    @Serializable object Auth
    @Serializable object Main
}

interface AuthRoutes {
    @Serializable object Login
    @Serializable object Register
}

interface MainGraph {
    @Serializable object Home
    @Serializable object Fav
    @Serializable object Profile
}

interface HomeRoutes {
    @Serializable object HomeList
}