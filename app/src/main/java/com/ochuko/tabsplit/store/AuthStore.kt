package com.ochuko.tabsplit.store


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.ochuko.tabsplit.models.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthStore(ctx: Context): ViewModel() {
    private  val JWT_KEY = "jwt_token"

    private  val masterKey = MasterKey
        .Builder(ctx)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

    private  val prefs = EncryptedSharedPreferences.create(ctx,"auth_store", masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

    private  val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    init {
        loadToken()
    }

    fun getToken(): String?{
        val token = prefs.getString(JWT_KEY ,null)
        return token
    }

    fun loadToken(){
        viewModelScope.launch {
            val token = getToken()

            if(token != null){
                println("{AuthStore} loadToken -> found token: $token")
                _authState.value = AuthState(token = token, loading = false)
            }else{
                println("[AuthStore] loadToken -> no token")
                _authState.value = AuthState(token = null, loading = false)
            }
        }
    }

    fun setToken(token:String?){
        viewModelScope.launch {
            if(token != null){
                prefs.edit().putString(JWT_KEY, token).apply()
                println("[AuthStore] setToken -> token set")
                _authState.value = AuthState(token = token, loading =  false)
            }else {
                prefs.edit().remove(JWT_KEY).apply()
                    println("[AuthState] setToken -> token cleared")
                    _authState.value = AuthState(token = null, loading = false)
            }
        }
    }

    fun login(token:String) {
        setToken(token)
    }

    fun logout(){
        setToken(null)
    }
}