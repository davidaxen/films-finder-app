package com.darvi.filmhunter.presentation.core.util

object RuntimeFormat {
    fun formatDuration(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return "${hours}h ${mins}m"
    }
}