package com.darvi.filmhunter.data.repository

import com.darvi.filmhunter.data.datasource.SupabaseDatabaseDataSource
import com.darvi.filmhunter.data.model.supabase.SessionDTO
import com.darvi.filmhunter.data.model.supabase.SessionMemberDTO
import com.darvi.filmhunter.data.model.supabase.SessionSwipeDTO
import com.darvi.filmhunter.data.model.supabase.SessionTitleDTO
import com.darvi.filmhunter.domain.entity.MatcherSessionEntity
import com.darvi.filmhunter.domain.repository.MatcherSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import javax.inject.Inject

class MatcherSessionRepositoryImpl @Inject constructor(
    private val database: SupabaseDatabaseDataSource
) : MatcherSessionRepository {

    override suspend fun createSession(
        code: String,
        createdBy: String,
        filters: Map<String, Any>
    ): Result<MatcherSessionEntity> {
        return try {
            // Convert filters map to JsonObject
            val filtersJson = buildJsonObject {
                filters.forEach { (key, value) ->
                    when (value) {
                        is String -> put(key, value)
                        is Int -> put(key, value)
                        is Boolean -> put(key, value)
                        is List<*> -> {
                            putJsonArray(key) {
                                value.forEach { item ->
                                    when (item) {
                                        is Int -> add(item)
                                        is String -> add(item)
                                        else -> add(item.toString())
                                    }
                                }
                            }
                        }
                        else -> put(key, value.toString())
                    }
                }
            }

            // Create session
            val sessionDTO = SessionDTO(
                code = code,
                createdBy = createdBy,
                status = "waiting",
                filters = filtersJson
            )

            val createdSession = database.createSession(sessionDTO)

            // Add creator as session member
            val memberDTO = SessionMemberDTO(
                sessionId = createdSession.id ?: "",
                userId = createdBy,
                state = "joined"
            )
            database.addSessionMember(memberDTO)

            Result.success(
                MatcherSessionEntity(
                    id = createdSession.id ?: "",
                    code = createdSession.code,
                    createdBy = createdSession.createdBy ?: "",
                    status = createdSession.status,
                    filters = filters
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelSession(sessionId: String): Result<Unit> {
        return try {
            database.cancelSession(sessionId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun joinSessionByCode(code: String, currentUserId: String): Result<MatcherSessionEntity> {
        return try {
            // Call RPC to join session
            val sessionId = database.joinSessionByCode(code)
            
            // Get session details
            val sessionDTO = database.getSessionById(sessionId)
            
            // Convert filters JsonObject back to Map
            val filtersMap = mutableMapOf<String, Any>()
            sessionDTO.filters.forEach { (key, value) ->
                when (value) {
                    is JsonPrimitive -> {
                        when {
                            value.isString -> filtersMap[key] = value.content
                            value.booleanOrNull != null -> filtersMap[key] = value.boolean
                            value.intOrNull != null -> filtersMap[key] = value.int
                            value.doubleOrNull != null -> filtersMap[key] = value.double
                            else -> filtersMap[key] = value.content
                        }
                    }

                    is JsonArray -> {
                        filtersMap[key] = value.map { element ->
                            when {
                                element is JsonPrimitive -> {
                                    element.intOrNull ?: element.content
                                }

                                else -> element.toString()
                            }
                        }
                    }

                    else -> filtersMap[key] = value.toString()
                }
            }
            
            Result.success(
                MatcherSessionEntity(
                    id = sessionDTO.id ?: "",
                    code = sessionDTO.code,
                    createdBy = sessionDTO.createdBy ?: "",
                    status = sessionDTO.status,
                    filters = filtersMap
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun initiateSession(sessionId: String): Result<Unit> {
        return try {
            database.updateSessionStatus(sessionId, "active")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun finishSession(sessionId: String): Result<Unit> {
        return try {
            database.updateSessionStatus(sessionId, "finished")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun leaveSession(sessionId: String, userId: String): Result<Unit> {
        return try {
            database.leaveSession(sessionId, userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun subscribeToSessionMembers(sessionId: String, currentUserId: String): Flow<String> {
        return database.subscribeToSessionMembers(sessionId, currentUserId)
    }

    override suspend fun unsubscribeFromSessionMembers(sessionId: String) {
        database.unsubscribeFromSessionMembers(sessionId)
    }

    override suspend fun subscribeToSessionStatus(sessionId: String): Flow<String> {
        return database.subscribeToSessionStatus(sessionId)
    }

    override suspend fun unsubscribeFromSessionStatus(sessionId: String) {
        database.unsubscribeFromSessionStatus(sessionId)
    }

    override suspend fun unsubscribeAllSessionListeners(sessionId: String) {
        database.unsubscribeAllSessionListeners(sessionId)
    }

    override suspend fun insertSessionTitles(sessionId: String, titles: List<Pair<Long, String>>): Result<Unit> {
        return try {
            val titleDTOs = titles.mapIndexed { index, (tmdbId, mediaType) ->
                SessionTitleDTO(
                    sessionId = sessionId,
                    tmdbId = tmdbId,
                    mediaType = mediaType,
                    pos = index
                )
            }
            database.insertSessionTitles(titleDTOs)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSessionTitles(sessionId: String): Result<List<Pair<Long, String>>> {
        return try {
            val titles = database.getSessionTitles(sessionId)
            Result.success(titles.map { it.tmdbId to it.mediaType })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun insertSessionSwipe(
        sessionId: String,
        userId: String,
        tmdbId: Long,
        mediaType: String,
        vote: String
    ): Result<Unit> {
        return try {
            val swipeDTO = SessionSwipeDTO(
                sessionId = sessionId,
                userId = userId,
                tmdbId = tmdbId,
                mediaType = mediaType,
                vote = vote
            )
            database.insertSessionSwipe(swipeDTO)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSessionSwipe(
        sessionId: String,
        userId: String,
        tmdbId: Long,
        mediaType: String
    ): Result<Unit> {
        return try {
            database.deleteSessionSwipe(sessionId, userId, tmdbId, mediaType)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSessionMembers(sessionId: String): Result<List<String>> {
        return try {
            val members = database.getSessionMembers(sessionId)
            Result.success(members.map { it.userId })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSessionSwipes(sessionId: String): Result<List<SessionSwipeDTO>> {
        return try {
            val swipes = database.getSessionSwipes(sessionId)
            Result.success(swipes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun subscribeToSessionSwipes(sessionId: String): Flow<SessionSwipeDTO> {
        return database.subscribeToSessionSwipes(sessionId)
    }

    override suspend fun unsubscribeFromSessionSwipes(sessionId: String) {
        database.unsubscribeFromSessionSwipes(sessionId)
    }
}

