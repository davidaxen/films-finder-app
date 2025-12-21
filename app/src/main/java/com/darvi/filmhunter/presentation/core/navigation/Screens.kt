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
    @Serializable object Search
    @Serializable object Match
    @Serializable object Saved
    @Serializable object Profile
    @Serializable data class Detail(val id: Int, val filmType: Int)
    @Serializable data class SeasonDetail(val seriesId: Int, val seasonNumber: Int)
}

interface HomeRoutes {
    @Serializable object MoviesList
    @Serializable object SeriesList
}

interface SearchRoutes {
    @Serializable object Main
    @Serializable data class QueryList(val query: String, val filmType: Int)
    @Serializable data class FilmsGenreList(val genre: Int, val platformId: Int?, val filmType: Int)
    @Serializable data class HomeFilmsList(val searchMethod: String, val filmType: Int)
}

interface MatchRoutes {
    @Serializable object Main
    @Serializable object FilmTypeSelection
    @Serializable object GenreSelection
    @Serializable object PlatformSelection
    @Serializable object Summary
}

interface SavedRoutes {
    @Serializable object Main
}

interface ProfileRoutes {
    @Serializable object Main
}