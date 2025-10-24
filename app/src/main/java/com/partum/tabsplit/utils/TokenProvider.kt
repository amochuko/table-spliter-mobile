package com.partum.tabsplit.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

object TokenProvider {
    private const val PREFS_NAME = "auth_store"
    private const val JWT_KEY = "jwt_token"

    @Volatile
    var token: String? = null
        private set

    private lateinit var prefs: SharedPreferences

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
    }

    fun saveToken(value: String?) {
        token = value

        prefs.edit {
            if (value == null) {
                remove(JWT_KEY)
            } else {
                putString(JWT_KEY, value)
            }
        }
    }
}