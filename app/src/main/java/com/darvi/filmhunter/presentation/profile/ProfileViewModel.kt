package com.darvi.filmhunter.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.entity.UserEntity
import com.darvi.filmhunter.domain.usecase.auth.GetCurrentUser
import com.darvi.filmhunter.domain.usecase.auth.SignOut
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getCurrentUser: GetCurrentUser,
    private val signOut: SignOut
): ViewModel() {
    val currentUser: StateFlow<UserEntity?> = getCurrentUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    
    fun onSignOut() {
        viewModelScope.launch {
            signOut()
        }
    }
}