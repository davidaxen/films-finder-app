package com.darvi.filmhunter.domain.usecase.matcher

import com.darvi.filmhunter.data.model.supabase.SessionSwipeDTO
import com.darvi.filmhunter.domain.repository.MatcherSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubscribeToSessionSwipes @Inject constructor(
    private val matcherSessionRepository: MatcherSessionRepository
) {
    suspend operator fun invoke(sessionId: String): Flow<SessionSwipeDTO> {
        return matcherSessionRepository.subscribeToSessionSwipes(sessionId)
    }
}

