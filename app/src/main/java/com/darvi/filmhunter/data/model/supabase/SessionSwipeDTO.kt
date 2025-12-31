package com.darvi.filmhunter.data.model.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionSwipeDTO(
    val id: String? = null,
    @SerialName("session_id") val sessionId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("tmdb_id") val tmdbId: Long,
    @SerialName("media_type") val mediaType: String,
    val vote: String,
    @SerialName("created_at") val createdAt: String? = null
)

