package com.darvi.filmhunter.presentation.core

import androidx.lifecycle.ViewModel
import com.darvi.filmhunter.domain.entity.UserEntity
import com.darvi.filmhunter.domain.usecase.auth.GetCurrentUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val getCurrentUser: GetCurrentUser
) : ViewModel() {
    val currentUser: Flow<UserEntity?> = getCurrentUser()
}

//sealed interface SessionState {
//    data object Loading : SessionState
//    data class Authenticated(val user: ) : SessionState
//    data object Unauthenticated : SessionState
//}
