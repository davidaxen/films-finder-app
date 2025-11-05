package com.darvi.filmhunter.data.repository

import com.darvi.filmhunter.data.datasource.SupabaseAuthDataSource
import com.darvi.filmhunter.data.model.toDomain
import com.darvi.filmhunter.domain.entity.UserEntity
import com.darvi.filmhunter.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val authDataSource: SupabaseAuthDataSource
): SessionRepository {
    private val _currentUser = MutableStateFlow<UserEntity?>(null)

    override fun getCurrentUser(): Flow<UserEntity?> {
        if (_currentUser.value == null) {
            authDataSource.getCurrentUser()
                ?.toDomain()
                ?.let { _currentUser.value = it }
        }
        return _currentUser
    }

    override fun setCurrentUser(user: UserEntity) {
        _currentUser.value = user
    }

    override suspend fun signOut() {
        authDataSource.signOut()
        _currentUser.value = null
    }
}