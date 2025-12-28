package com.darvi.filmhunter.domain.usecase.matcher

import com.darvi.filmhunter.data.model.supabase.SessionSwipeDTO
import com.darvi.filmhunter.domain.repository.MatcherSessionRepository
import javax.inject.Inject

class GetSessionSwipes @Inject constructor(
    private val matcherSessionRepository: MatcherSessionRepository
) {
    suspend operator fun invoke(sessionId: String): Result<List<SessionSwipeDTO>> {
        return matcherSessionRepository.getSessionSwipes(sessionId)
    }
}

