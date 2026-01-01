package com.darvi.filmhunter.domain.usecase.matcher

import com.darvi.filmhunter.domain.repository.MatcherSessionRepository
import javax.inject.Inject

class GetSessionTitles @Inject constructor(
    private val matcherSessionRepository: MatcherSessionRepository
) {
    suspend operator fun invoke(sessionId: String): Result<List<Pair<Long, String>>> {
        return matcherSessionRepository.getSessionTitles(sessionId)
    }
}

