package com.darvi.filmhunter.domain.entity

data class MatcherSessionEntity(
    val id: String,
    val code: String,
    val createdBy: String,
    val status: String,
    val filters: Map<String, Any>,
    val createdAt: String? = null
)