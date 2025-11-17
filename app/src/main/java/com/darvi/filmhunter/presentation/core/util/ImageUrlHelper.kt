package com.darvi.filmhunter.presentation.core.util

object ImageUrlHelper {
    fun getOriginalUrl(path: String): String {
        return "https://image.tmdb.org/t/p/original$path"
    }

    fun getW342Url(path: String): String {
        return "https://image.tmdb.org/t/p/w342$path"
    }
}