package com.partum.tabsplit.ui.auth

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.partum.tabsplit.data.api.ApiClient
import com.partum.tabsplit.data.api.AuthApi
import com.partum.tabsplit.data.api.UserApi
import com.partum.tabsplit.data.model.User
import com.partum.tabsplit.data.repository.AuthRepository
import com.partum.tabsplit.data.repository.UserRepository
import com.partum.tabsplit.utils.Config
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    @SuppressLint("StaticFieldLeak")
    val ctx: Context = getApplication<Application>().applicationContext;

    private val JwtKey = "jwt_token"

    private val masterKey = MasterKey
        .Builder(ctx)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        ctx, "auth_store", masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    private val authApi = ApiClient.create<AuthApi>(baseUrl = Config.API_BASE_URL)
    private val authRepo = AuthRepository(authApi, ctx)


    private val userApi = ApiClient.create<UserApi>(getToken(), baseUrl = Config.API_BASE_URL)
    private val userRepo = UserRepository(userApi)


    init {
        loadToken()
    }

    fun getToken(): String? {
        return prefs.getString(JwtKey, null)
    }

    fun saveToken(token: String?) {
        prefs.edit().apply {
            if (token != null) {
                putString(JwtKey, token)
            } else {
                remove(JwtKey)
            }
        }.apply()
    }

    fun loadToken() {
        viewModelScope.launch {
            val token = getToken()

            _uiState.value = _uiState.value.copy(
                token,
                isLoggedIn = token != null,
                loading = false
            )
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

                saveToken(token)
                updateState(user, token, isLoggedIn = true)

                Log.d("[AuthViewModel] Signup successful -> user:", token)
            } else {
                updateState(error = "Signup failed: null result")
            }
        } catch (e: Exception) {
            Log.e("[AuthViewModel]", "Signup failed", e)
            updateState(error = e.localizedMessage)
        }
    }

    fun logout() = viewModelScope.launch {
        saveToken(token = null)
        updateState(user = null, token = null, isLoggedIn = false)
    }

    suspend fun login(email: String, password: String): Boolean {
        updateState(loading = true)

        return try {
            val result = authRepo.login(email, password)
            if (result != null) {
                val (user, token) = result
                saveToken(token)

                updateState(user, token, true)
                println("[AuthViewModel] Login successful -> user: ${user.email}")
                true
            } else {
                updateState(error = "Invalid credentials", loading = false)
                // This block is executed if loginResult is null
                // Handle the failed
                println("[AuthViewModel] Login failed -> null result")
                false
            }
        } catch (e: Exception) {
            Log.e("AppStore", "Login failed", e)
            updateState(error = e.localizedMessage, loading = false)
            false
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
            } else
                false
        } catch (e: Exception) {
            Log.e("AuthViewModel", "failed to update zaddr $e")
            updateState(error = e.localizedMessage)
            false
        }
    }
}