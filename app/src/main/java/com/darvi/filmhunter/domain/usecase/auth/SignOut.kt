package com.darvi.filmhunter.domain.usecase.auth

import com.darvi.filmhunter.domain.repository.SessionRepository
import javax.inject.Inject

class SignOut @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke() {
        sessionRepository.signOut()
    }
}

