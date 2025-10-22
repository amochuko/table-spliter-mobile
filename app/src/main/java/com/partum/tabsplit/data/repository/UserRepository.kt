package com.ochuko.tabsplit.data.repository

import com.ochuko.tabsplit.data.api.UserApi
import com.ochuko.tabsplit.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val api: UserApi, ) {

    suspend fun updateZAddr(zaddr: String): User? = withContext(Dispatchers.IO) {
        val res = api.updateZAddr(mapOf("zaddr" to zaddr))

        if (res.isSuccessful) {
            val user = res.body()?.user

            user
        } else null
    }

    suspend fun deleteZAddr(): Boolean = withContext(Dispatchers.IO) {
        val res = api.deleteZAddr()

        if (res.isSuccessful) {

            true
        } else false
    }

}