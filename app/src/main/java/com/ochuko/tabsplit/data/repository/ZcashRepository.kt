package com.ochuko.tabsplit.data.repository


import android.content.Context
import com.ochuko.tabsplit.data.api.ApiClient
import com.ochuko.tabsplit.data.api.UserDto
import com.ochuko.tabsplit.data.local.SecurePrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ZcashRepository(private val context: Context) {
    private val api = ApiClient.api

    suspend fun updateZAddr(zaddr: String): UserDto? = withContext(Dispatchers.IO) {
        val res = api.updateZAddr(mapOf("zaddr" to zaddr))
        if (res.isSuccessful) {
            val user = res.body()?.user
            if (user?.zaddr != null) {
                SecurePrefs.saveZAddr(context, user.zaddr!!)
            }
            user
        } else null
    }

    suspend fun deleteZAddr(): Boolean = withContext(Dispatchers.IO) {
        val res = api.deleteZAddr()
        if (res.isSuccessful) {
            SecurePrefs.clearZAddr(context)
            true
        } else false
    }

    fun loadSavedZAddr(): String? = SecurePrefs.getZAddr(context)
}
