package com.ochuko.tabsplit.store

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ochuko.tabsplit.data.api.ApiClient
import com.ochuko.tabsplit.data.api.AuthApi
import com.ochuko.tabsplit.data.api.AuthResponse
import com.ochuko.tabsplit.data.local.SecurePrefs
import com.ochuko.tabsplit.data.repository.AuthRepository
import com.ochuko.tabsplit.models.AuthUiState
import com.ochuko.tabsplit.models.User
import com.ochuko.tabsplit.models.UserToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class AuthViewModel(ctx: Context) : ViewModel() {
    private val repo = AuthRepository(
        ApiClient.getRetrofit(ctx, "http://10.0.0.2").create
            (
            AuthApi::class
                .java
        ), ctx
    )

    private val _uiState = MutableStateFlow(
        AuthUiState(
            token = repo.getSavedToken(),
            isLoggedIn = repo.getSavedToken() != null
        )
    )

    val uiState: StateFlow<AuthUiState> = _uiState

    val token: StateFlow<String?> = _uiState.map { it.token }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        _uiState.value.token
    )


    val isLoggedIn: StateFlow<Boolean> = _uiState.map { it.isLoggedIn }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        _uiState.value.isLoggedIn
    )

    suspend fun login(email: String, password: String): UserToken? {

        val res = repo.login(email, password)

        return if (res != null) {
            val token = repo.getSavedToken()
            val (user, _t) = res

            _uiState.value = AuthUiState(token = token, isLoggedIn = true)
            UserToken(user, token.toString())
        } else {
            _uiState.value = AuthUiState(error = "Login failed")
            null
        }
    }

    suspend fun register(email: String, password: String): UserToken? {

        val res = repo.signup(email, password)

        return if (res != null) {
            val token = repo.getSavedToken()
            val (user, _t) = res

            _uiState.value = AuthUiState(token = token, isLoggedIn = true)
            UserToken(user, token.toString())
        } else {
            _uiState.value = AuthUiState(error = "Login failed")
            null
        }
    }

    fun logout() {
        repo.logout()
        _uiState.value = AuthUiState(isLoggedIn = false, token = null)
    }

//
//    fun clearToken() {
//        SecurePrefs.clearToken(ctx)
//    }

}
