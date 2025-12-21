package com.darvi.filmhunter.data.model.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class SessionDTO(
    val id: String? = null,
    val code: String,
    @SerialName("created_by") val createdBy: String?,
    val status: String = "waiting",
    val filters: JsonObject = JsonObject(emptyMap()),
    val seed: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("expires_at") val expiresAt: String? = null
)

