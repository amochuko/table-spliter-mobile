package com.partum.tabsplit.ui.auth

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.partum.tabsplit.data.api.ApiClient
import com.partum.tabsplit.data.api.AuthApi
import com.partum.tabsplit.data.api.UserApi
import com.partum.tabsplit.data.model.User
import com.partum.tabsplit.data.repository.AuthRepository
import com.partum.tabsplit.data.repository.UserRepository
import com.partum.tabsplit.utils.Config
import com.partum.tabsplit.utils.AuthSessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    @SuppressLint("StaticFieldLeak")
    val ctx: Context = getApplication<Application>().applicationContext;

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    private val authApi = ApiClient.create<AuthApi>(baseUrl = Config.API_BASE_URL)
    private val authRepo = AuthRepository(authApi, ctx)
    private val userApi = ApiClient.create<UserApi>(getToken(), baseUrl = Config.API_BASE_URL)
    private val userRepo = UserRepository(userApi)


    init {
        loadAuthSession()
    }

    fun getToken(): String? {
        return AuthSessionManager.token
    }

    fun saveSession(token: String?, user: User) {
        AuthSessionManager.save(token, user)
    }

    fun clearSession() {
        AuthSessionManager.clear()
    }

    fun loadAuthSession() {
        viewModelScope.launch {
            val token = getToken()
            val user = AuthSessionManager.user

            if (token != null && user != null) {
                updateState(user, token, isLoggedIn = true, loading = false)
            }
        }
    }

    private fun updateState(
        user: User? = _uiState.value.user,
        token: String? = _uiState.value.token,
        isLoggedIn: Boolean = _uiState.value.isLoggedIn,
        loading: Boolean = false,
        error: String? = null,
    ) {
        _uiState.value = _uiState.value.copy(
            token, user, isLoggedIn, error, loading
        )
    }

    fun signup(email: String, password: String) = viewModelScope.launch {
        updateState(loading = true)

        try {
            val result = authRepo.signup(email, password)
            if (result != null) {
                val (user, token) = result

                saveSession(token, user)
                updateState(user, token, isLoggedIn = true)

                Log.d("[AuthViewModel] Signup successful -> user:", "$token, ${user.toString()} ")
            } else {
                updateState(error = "Signup failed: null result")
            }
        } catch (e: Exception) {
            Log.e("[AuthViewModel]", "Signup failed", e)
            updateState(error = e.localizedMessage)
        }
    }

    fun logout() = viewModelScope.launch {
        clearSession()
        updateState(user = null, token = null, isLoggedIn = false)
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        updateState(loading = true)

        try {
            val result = authRepo.login(email, password)
            if (result != null) {

                val (user, token) = result

                saveSession(token, user)
                updateState(user, token, true)

                Log.d("[AuthViewModel] Login successful -> user:", "$token, ${user.toString()}")
            } else {
                // Handle the failed
                updateState(error = "Invalid credentials", loading = false)

                println("[AuthViewModel] Login failed -> null result")
            }
        } catch (e: Exception) {
            Log.e("[AuthViewModel]", "Login failed", e)
            updateState(error = e.localizedMessage, loading = false)
        }
    }

    suspend fun updateZAddr(zaddr: String): Boolean {
        return try {
            val res = userRepo.updateZAddr(zaddr)
            if (res != null) {
                val currentUser = _uiState.value.user?.copy(zaddr = zaddr)

                if (currentUser != null) {
                    updateState(user = currentUser)
                }
                true
            } else false
        } catch (e: Exception) {
            Log.e("AuthViewModel", "failed to update zaddr $e")
            updateState(error = e.localizedMessage)
            false
        }
    }

    fun updateProfile(username: String, email: String?, zaddr: String?) = viewModelScope.launch {
        _uiState.update { it.copy(loading = true) }

        try {
            val response = authRepo.updateProfile(username, email, zaddr)

            response?.let { res ->

                _uiState.update {
                    it.copy(
                        loading = false,
                        user = it.user?.copy(
                            username = res.user.username!!,
                            zaddr = res.user.zaddr,
                            email = res.user.email!!
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("AuthViewModel::updateProfile", "Failed to update profile", e)
            _uiState.update {
                it.copy(
                    loading = false, error = e.message
                )
            }
        }
    }
}