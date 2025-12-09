package com.darvi.filmhunter.presentation.core.model

enum class SearchMethod(val value: String, val displayName: String = "") {
    TITLE("title"),
    GENRE("genre"),
    POPULAR("popular", "populares"),
    TOP_RATED("top_rated", "mejor valoradas"),
    ON_SCREEN("on_screen", "en pantalla");

    companion object {
        fun fromValue(name: String): SearchMethod? =
            SearchMethod.entries.firstOrNull { it.value == name }
    }
}