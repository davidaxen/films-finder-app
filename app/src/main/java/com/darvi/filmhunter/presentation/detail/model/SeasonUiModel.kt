package com.darvi.filmhunter.presentation.detail.model

data class SeasonUiModel(
    val id: Int,
    val name: String,
    val seasonNumber: Int,
    val episodeCount: Int,
    val posterPath: String? = null
)