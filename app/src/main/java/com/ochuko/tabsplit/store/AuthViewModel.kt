package com.ochuko.tabsplit.store

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ochuko.tabsplit.data.api.AuthApi
import com.ochuko.tabsplit.data.repository.AuthRepository
import com.ochuko.tabsplit.models.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AuthViewModel(ctx: Context) : ViewModel() {
    private val repo = AuthRepository(AuthApi, ctx)

    private val _uiState = MutableStateFlow(
        AuthUiState(
            token = repo.getSavedToken(),
            isLoggedIn = repo.getSavedToken() != null
        )
    )

    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val user = repo.login(email, password)
            if (user != null) {
                val token = repo.getSavedToken()
                _uiState.value = AuthUiState(token = token, isLoggedIn = true)
            } else {
                _uiState.value = AuthUiState(error = "Login failed")
            }
        }
    }

    fun logout() {
        repo.logout()
        _uiState.value = AuthUiState(isLoggedIn = false, token = null)
    }
}
