package com.partum.tabsplit.data.repository


import android.content.Context
import com.partum.tabsplit.data.api.AuthApi
import com.partum.tabsplit.data.api.LoginRequest
import com.partum.tabsplit.data.api.RegisterRequest
import com.partum.tabsplit.data.model.User


class AuthRepository(private val api: AuthApi, private val ctx: Context) {

    suspend fun login(email: String, password: String): Pair<User, String>? {
        val res = api.login(LoginRequest(email, password))

        if (res.isSuccessful) {
            val (body, token) = res.body() ?: return null
            val user = User(body.id, body.username, body.email, body.zaddr)

            return Pair(user, token)
        }

        return null;
    }

    suspend fun signup(email: String, password: String): Pair<User, String>? {
        val res = api.register(RegisterRequest(email, password))

        if (res.isSuccessful) {
            val (body, token) = res.body() ?: return null

            return Pair(body, token)
        }

        return null;
    }
}
