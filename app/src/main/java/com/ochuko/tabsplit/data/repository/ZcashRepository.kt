package com.ochuko.tabsplit.data.repository


import android.content.Context
import com.ochuko.tabsplit.data.api.ApiClient
import com.ochuko.tabsplit.data.api.SessionApi
import com.ochuko.tabsplit.data.api.UserApi
import com.ochuko.tabsplit.data.local.SecurePrefs
import com.ochuko.tabsplit.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ZcashRepository(private val api: UserApi, private val ctx: Context) {

    suspend fun updateZAddr(zaddr: String): User? = withContext(Dispatchers.IO) {
        val res = api.updateZAddr(mapOf("zaddr" to zaddr))

        if (res.isSuccessful) {
            val user = res.body()?.user

            if (user?.zaddr != null) {
                SecurePrefs.saveZAddr(ctx, user.zaddr!!)
            }

            user
        } else null
    }

    suspend fun deleteZAddr(): Boolean = withContext(Dispatchers.IO) {
        val res = api.deleteZAddr()

        if (res.isSuccessful) {
            SecurePrefs.clearZAddr(ctx)
            true
        } else false
    }

    fun loadSavedZAddr(): String? = SecurePrefs.getZAddr(ctx)
}
