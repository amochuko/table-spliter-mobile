package com.partum.tabsplit.data.local


import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecurePrefs {
    private const val PREF_NAME = "secure_prefs"
    private const val KEY_ZADDR = "zaddr"
    private const val KEY_TOKEN = "auth_token"

    private fun getPrefs(ctx: Context) =
        EncryptedSharedPreferences.create(
            ctx,
            PREF_NAME,
            MasterKey.Builder(ctx)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    // ZAddr
    fun saveZAddr(ctx: Context, zaddr: String) {
        getPrefs(ctx).edit().putString(KEY_ZADDR, zaddr).apply()
    }

    fun getZAddr(ctx: Context): String? =
        getPrefs(ctx).getString(KEY_ZADDR, null)

    fun clearZAddr(ctx: Context) {
        getPrefs(ctx).edit().remove(KEY_ZADDR).apply()
    }

    // Token
    fun saveToken(ctx: Context, token: String) {
        getPrefs(ctx).edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(ctx: Context): String? =
        getPrefs(ctx).getString(KEY_TOKEN, null)

    fun clearToken(ctx: Context) {
        getPrefs(ctx).edit().remove(KEY_TOKEN).apply()
    }
}