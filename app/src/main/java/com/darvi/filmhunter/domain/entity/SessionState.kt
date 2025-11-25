package com.darvi.filmhunter.domain.entity

sealed interface SessionState {
    data object Loading : SessionState
    data object Unauthenticated : SessionState
    data class Authenticated(val user: UserEntity) : SessionState
}