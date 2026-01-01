package com.darvi.filmhunter.presentation.core.model

enum class MovieListSection(val path: String, val searchMethod: String) {
    POPULAR("popular","popular"),
    TOP_RATED("top_rated","top_rated"),
    NOW_PLAYING("now_playing", "on_screen"),
    UPCOMING("upcoming", "");

    companion object {
        fun fromSearchMethod(method: String): MovieListSection =
            entries.firstOrNull { it.searchMethod == method} ?: POPULAR
    }
}