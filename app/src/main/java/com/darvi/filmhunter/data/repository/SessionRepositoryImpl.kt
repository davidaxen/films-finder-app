package com.darvi.filmhunter.data.repository

import com.darvi.filmhunter.data.datasource.SupabaseAuthDataSource
import com.darvi.filmhunter.data.model.UserModel
import com.darvi.filmhunter.data.model.toDomain
import com.darvi.filmhunter.domain.entity.SessionState
import com.darvi.filmhunter.domain.entity.UserEntity
import com.darvi.filmhunter.domain.repository.SessionRepository
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val authDataSource: SupabaseAuthDataSource
): SessionRepository {
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    override fun getCurrentUser(): StateFlow<UserEntity?> = _currentUser

    override fun observeSession(): Flow<SessionState> {
        return authDataSource.getCurrentUserFlow() // Flow<SessionStatus>
            .map { status ->
                delay(100)
                when (status) {
                    is SessionStatus.Authenticated -> {
                        val user = status.session.user?.let {
                            UserModel(
                                uid = it.id,
                                email = it.email ?: "",
                                displayName = (it.userMetadata?.get("name") ?: "") as String
                            ).toDomain()
                        }

                        if (user != null) {
                            _currentUser.value = user
                            SessionState.Authenticated(user)
                        } else {
                            SessionState.Unauthenticated
                        }
                    }

                    is SessionStatus.NotAuthenticated -> SessionState.Unauthenticated
                    else -> SessionState.Loading
                }
            }
            .distinctUntilChanged()
    }

    override fun setCurrentUser(user: UserEntity) {
        _currentUser.value = user
    }

    override suspend fun signOut() {
        authDataSource.signOut()
        _currentUser.value = null
    }
}