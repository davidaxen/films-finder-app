package com.darvi.filmhunter.data.model.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionMemberDTO(
    val id: String? = null,
    @SerialName("session_id") val sessionId: String,
    @SerialName("user_id") val userId: String,
    val state: String = "joined",
    @SerialName("joined_at") val joinedAt: String? = null
)

