package com.ochuko.tabsplit.data.repository


import com.ochuko.tabsplit.data.api.ApiClient
import com.ochuko.tabsplit.data.api.UserApi
import com.ochuko.tabsplit.models.User
import com.ochuko.tabsplit.utils.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val api: UserApi) {

    companion object {
        val userApi = ApiClient.create<UserApi>(baseUrl = Config.API_BASE_URL)
    }

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