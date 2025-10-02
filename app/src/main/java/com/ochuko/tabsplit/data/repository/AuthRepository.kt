package com.ochuko.tabsplit.data.repository



import android.content.Context

import com.ochuko.tabsplit.data.api.AuthApi
import com.ochuko.tabsplit.data.api.LoginRequest
import com.ochuko.tabsplit.data.local.SecurePrefs
import com.ochuko.tabsplit.models.User


class AuthRepository(private val api:AuthApi, private val ctx: Context) {

    private val prefs = ctx.getSharedPreferences("auth", Context.MODE_PRIVATE)

    suspend fun login(email: String, password: String): User? {
        val res = api.login(LoginRequest(email, password))

        if(res.isSuccessful){
            res.body()?.token?.let {saveToken(it)}

            res.body()
        }

        return  null;
    }


    fun getSavedToken(): String? = prefs.getString("token", null)


    private fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    fun logout() {
        SecurePrefs.clearToken(ctx)
        SecurePrefs.clearZAddr(ctx) // also clear zaddr if tied to account
    }
}
