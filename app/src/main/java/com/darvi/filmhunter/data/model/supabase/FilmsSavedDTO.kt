package com.darvi.filmhunter.data.model.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FilmsSavedDTO(
    @SerialName("film_id") val filmId: Int,
    @SerialName("film_type") val filmType: String,
    @SerialName("created_at") val createdAt: String? = null
)
