package com.ochuko.tabsplit.store


import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.ochuko.tabsplit.models.AuthState
import com.ochuko.tabsplit.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import com.ochuko.tabsplit.data.api.ApiClient
import com.ochuko.tabsplit.data.api.AuthApi
import com.ochuko.tabsplit.data.repository.AuthRepository
import com.ochuko.tabsplit.utils.Config

class AuthStore(app: Application) : AndroidViewModel(app) {
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

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    private val authApi = ApiClient.create<AuthApi>(baseUrl = Config.API_BASE_URL)
    private val authRepo = AuthRepository(authApi, ctx)


    init {
        loadToken()
    }

    fun getToken(): String? {
        return prefs.getString(JwtKey, null)
    }

    fun loadToken() {
        viewModelScope.launch {
            val token = getToken()
            _authState.value = AuthState(token = token, loading = false)
        }
    }

    fun setUser(user: User?, token: String?) {
        viewModelScope.launch {
            if (token != null) {
                prefs.edit() { putString(JwtKey, token) }
                println("[AuthStore] Token saved")
            } else {
                prefs.edit() { remove(JwtKey) }
                println("[AuthStore] Token cleared")
            }

            _authState.value = AuthState(user, token, false)
        }
    }

    fun signup(email: String, password: String) = viewModelScope.launch {
        try {
            val result = authRepo.signup(email, password)
            if (result != null) {
                val (user, token) = result
                setUser(user, token)

                println("[AuthStore] Signup successful -> user: ${user.email}")
            } else {
                println("[AuthStore] Signup failed -> null result")
            }
        } catch (e: Exception) {
            Log.e("AppStore", "Register failed", e)
        }
    }


    fun logout(email: String, password: String) = viewModelScope.launch {
        try {
            setUser(null, null)
        } catch (e: Exception) {
            Log.e("AppStore", "Logout failed", e)
        }
    }

    suspend fun login(email: String, password: String): Boolean {

        return try {
            val result = authRepo.login(email, password)

            if (result != null) {
                val (user, token) = result
                setUser(user, token)

                println("[AuthStore] Login successful -> user: ${user.email}")
                true
            } else {
                // This block is executed if loginResult is null
                // Handle the failed
                println("[AuthStore] Login failed -> null result")
                false
            }
        } catch (e: Exception) {
            Log.e("AppStore", "Login failed", e)
            false
        }
    }

}