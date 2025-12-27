package com.darvi.filmhunter.data.datasource

import android.util.Log
import com.darvi.filmhunter.data.model.supabase.FilmsSavedDTO
import com.darvi.filmhunter.data.model.supabase.SessionDTO
import com.darvi.filmhunter.data.model.supabase.SessionMemberDTO
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

class SupabaseDatabaseDataSourceImpl @Inject constructor(
    private val database: Postgrest,
    private val realtime: Realtime
): SupabaseDatabaseDataSource {
    private val dataSourceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val channels = mutableMapOf<String, RealtimeChannel>()
    private val _memberJoinedFlow = MutableSharedFlow<String>(replay = 0)
    object Tables {
        const val SAVED_FILMS = "saved_films"
        const val SESSIONS = "sessions"
        const val SESSION_MEMBERS = "session_members"
    }

    override suspend fun saveFilm(filmId: Int, filmType: String) {
        val filmToSave = FilmsSavedDTO(
            filmId = filmId,
            filmType = filmType,
        )
        database.from(Tables.SAVED_FILMS).insert(filmToSave)
    }

    override suspend fun removeSavedFilm(filmId: Int, filmType: String) {
        database.from(Tables.SAVED_FILMS)
            .delete {
                filter {
                    eq("film_id", filmId)
                    eq("film_type", filmType)
                }
            }
    }

    override suspend fun isFilmSaved(filmId: Int, filmType: String): Boolean {
        val resp = database
            .from(Tables.SAVED_FILMS)
            .select {
                filter {
                    eq("film_id", filmId)
                    eq("film_type", filmType)
                }
                count(Count.EXACT)
            }
            .countOrNull()

        return (resp ?: 0) > 0
    }

    override suspend fun getSavedFilmsId(filmType: String): List<FilmsSavedDTO> {
        val resp = database
            .from(Tables.SAVED_FILMS)
            .select {
                filter {
                    eq("film_type", filmType)
                }
            }
            .decodeList<FilmsSavedDTO>()

        return resp
    }

    override suspend fun createSession(session: SessionDTO): SessionDTO {
        val response = database
            .from(Tables.SESSIONS)
            .insert(session) {
                select()
            }
            .decodeSingle<SessionDTO>()
        
        return response
    }

    override suspend fun addSessionMember(member: SessionMemberDTO): SessionMemberDTO {
        val response = database
            .from(Tables.SESSION_MEMBERS)
            .insert(member) {
                select()
            }
            .decodeSingle<SessionMemberDTO>()
        
        return response
    }

    override suspend fun cancelSession(sessionId: String) {
        database
            .from(Tables.SESSIONS)
            .update ({
                set("status", "cancelled")
            }) {
                filter {
                    eq("id", sessionId)
                }
            }
    }

    override suspend fun joinSessionByCode(code: String): String {
        val response = database
            .rpc("join_session_by_code", parameters = mapOf("p_code" to code))
            .decodeAs<String>()

        return response
    }

    override suspend fun getSessionById(sessionId: String): SessionDTO {
        val response = database
            .from(Tables.SESSIONS)
            .select {
                filter {
                    eq("id", sessionId)
                }
            }
            .decodeSingle<SessionDTO>()
        
        return response
    }

    override suspend fun updateSessionStatus(sessionId: String, status: String) {
        database
            .from(Tables.SESSIONS)
            .update ({
                set("status", status)
            }) {
                filter {
                    eq("id", sessionId)
                }
            }
    }

    override suspend fun subscribeToSessionMembers(sessionId: String, currentUserId: String): Flow<String> {
        val channelKey = "session_members_$sessionId"
        
        // Unsubscribe if already subscribed
        unsubscribeFromSessionMembers(sessionId)
        
        dataSourceScope.launch {
            try {
                val channel = realtime.channel(channelKey)
                Log.d("SupabaseDatabaseDataSourceImpl", "channel: $channel")
                
                // Listen for INSERT events on session_members table
                channel.postgresChangeFlow<PostgresAction.Insert>(
                    schema = "public",
                ) {
                    table = "session_members"
                }
                .onEach { change ->
                    // Check if the inserted user is not the current user
                    val userIdElement = change.record["user_id"]
                    val insertedUserId = (userIdElement as? JsonPrimitive)?.content
                    
                    Log.d("SupabaseDatabaseDataSourceImpl", "Change received - insertedUserId: $insertedUserId, currentUserId: $currentUserId")
                    
                    if (insertedUserId != null && insertedUserId != currentUserId) {
                        Log.d("SupabaseDatabaseDataSourceImpl", "Another user joined! Emitting sessionId: $sessionId")
                        _memberJoinedFlow.emit(sessionId)
                    }
                }
                .launchIn(dataSourceScope)
                
                channel.subscribe()
                Log.d("SupabaseDatabaseDataSourceImpl", "Subscribed to channel: $channelKey")
                channels[channelKey] = channel
            } catch (e: Exception) {
                // Handle error silently or log it
            }
        }
        
        return _memberJoinedFlow.asSharedFlow()
    }

    override suspend fun unsubscribeFromSessionMembers(sessionId: String) {
        val channelKey = "session_members_$sessionId"
        channels[channelKey]?.let { channel ->
            try {
                channel.unsubscribe()
                channels.remove(channelKey)
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }
}