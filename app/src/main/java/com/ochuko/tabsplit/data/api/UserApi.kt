package com.ochuko.tabsplit.data.api

import com.ochuko.tabsplit.models.User
import retrofit2.Response
import retrofit2.http.*


data class UserResponse(
    val user: User
)


interface UserApi {

    @PATCH("/users/me")
    suspend fun updateZAddr(@Body body: Map<String, String>): Response<UserResponse>

    @DELETE("/users/me/zaddr")
    suspend fun deleteZAddr(): Response<Unit>
}