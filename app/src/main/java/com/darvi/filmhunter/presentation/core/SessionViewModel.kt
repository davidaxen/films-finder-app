package com.darvi.filmhunter.presentation.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darvi.filmhunter.domain.entity.SessionState
import com.darvi.filmhunter.domain.usecase.auth.GetCurrentSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
//    private val getCurrentUser: GetCurrentUser,
    getCurrentSession: GetCurrentSession
) : ViewModel() {

    val sessionState: StateFlow<SessionState> =
        getCurrentSession()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = SessionState.Loading
            )

}
