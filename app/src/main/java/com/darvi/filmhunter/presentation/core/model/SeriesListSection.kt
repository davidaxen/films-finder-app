package com.darvi.filmhunter.presentation.core.model

enum class SeriesListSection(val path: String, val searchMethod: String) {
    POPULAR("popular", "popular"),
    TOP_RATED("top_rated", "top_rated"),
    AIRING_TODAY("airing_today", "on_screen"),
    ON_THE_AIR("on_the_air", "");

    companion object {
        fun fromSearchMethod(method: String): SeriesListSection =
            entries.firstOrNull { it.searchMethod == method} ?: POPULAR
    }
}