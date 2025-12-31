package com.darvi.filmhunter.domain.usecase.matcher

import com.darvi.filmhunter.domain.repository.MatcherSessionRepository
import javax.inject.Inject

class SaveSessionSwipe @Inject constructor(
    private val matcherSessionRepository: MatcherSessionRepository
) {
    suspend operator fun invoke(
        sessionId: String,
        userId: String,
        tmdbId: Long,
        mediaType: String,
        vote: String
    ): Result<Unit> {
        // Delete existing swipe if any (to allow changing vote)
        matcherSessionRepository.deleteSessionSwipe(
            sessionId = sessionId,
            userId = userId,
            tmdbId = tmdbId,
            mediaType = mediaType
        ).onFailure {
            // Ignore error if no existing swipe (it's fine)
        }
        
        // Insert new swipe
        return matcherSessionRepository.insertSessionSwipe(
            sessionId = sessionId,
            userId = userId,
            tmdbId = tmdbId,
            mediaType = mediaType,
            vote = vote
        )
    }
}

