package com.ochuko.tabsplit.data.repository


import android.content.Context

import com.ochuko.tabsplit.data.api.AuthApi
import com.ochuko.tabsplit.data.api.LoginRequest
import com.ochuko.tabsplit.data.api.RegisterRequest
import com.ochuko.tabsplit.data.local.SecurePrefs
import com.ochuko.tabsplit.models.User


class AuthRepository(private val api: AuthApi, private val ctx: Context) {

    private val prefs = ctx.getSharedPreferences("auth", Context.MODE_PRIVATE)


    suspend fun login(email: String, password: String): Pair<User, String>? {
        val res = api.login(LoginRequest(email, password))

        if (res.isSuccessful) {
            val (body, token) = res.body() ?: return null
            val user = User(body.id, body.username, body.email, body.zaddr)

            saveToken(token)
            return Pair(user, token)
        }

        return null;
    }

    suspend fun signup(email: String, password: String): Pair<User, String>? {
        val res = api.register(RegisterRequest(email, password))

        if (res.isSuccessful) {
            val (body, token) = res.body() ?: return null
            val user = User(body.id, body.username, body.email, body.zaddr)

            saveToken(token)
            return Pair(user, token)
        }

        return null;
    }


    fun getSavedToken(): String? = prefs.getString("token", null)


    private fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    fun logout() {
        SecurePrefs.clearToken(ctx)
//        SecurePrefs.clearZAddr(ctx) // also clear zaddr if tied to account
    }
}
