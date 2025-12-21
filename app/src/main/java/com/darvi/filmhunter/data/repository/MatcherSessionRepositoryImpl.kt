package com.darvi.filmhunter.data.repository

import com.darvi.filmhunter.data.datasource.SupabaseDatabaseDataSource
import com.darvi.filmhunter.data.model.supabase.SessionDTO
import com.darvi.filmhunter.data.model.supabase.SessionMemberDTO
import com.darvi.filmhunter.domain.entity.MatcherSessionEntity
import com.darvi.filmhunter.domain.repository.MatcherSessionRepository
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
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
}

