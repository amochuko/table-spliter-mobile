package com.partum.tabsplit.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit
import com.google.gson.Gson
import com.partum.tabsplit.data.model.User

object AuthSessionManager {
    private const val PREFS_NAME = "auth_store"
    private const val JWT_KEY = "jwt_token"
    private const val USER_KEY = "user_json"

    @Volatile
    var token: String? = null
        private set

    @Volatile
    var user: User? = null
        private set

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    fun init(ctx: Context) {
        // Use EncryptedSharedPreferences for security
        prefs = EncryptedSharedPreferences.create(
            ctx,
            PREFS_NAME,
            MasterKey.Builder(ctx).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        token = prefs.getString(JWT_KEY, null)
        prefs.getString(USER_KEY, null)?.let {
            user = gson.fromJson<User>(it, User::class.java)
        }
    }

    fun save(token: String?, user: User?) {
        this.token = token
        this.user = user

        prefs.edit {
            if (token == null) {
                remove(JWT_KEY)
            } else {
                putString(JWT_KEY, token)
            }

            if (user == null) remove(USER_KEY) else putString(USER_KEY, gson.toJson(user))
        }

    }

    fun isValidSession(): Boolean {
        return token != null && user != null
    }

    fun clear() {
        save(null, null)
    }
}