package com.darvi.filmhunter.data.model.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionTitleDTO(
    val id: String? = null,
    @SerialName("session_id") val sessionId: String,
    @SerialName("tmdb_id") val tmdbId: Long,
    @SerialName("media_type") val mediaType: String,
    val pos: Int,
    @SerialName("created_at") val createdAt: String? = null
)

