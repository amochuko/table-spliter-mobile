package com.ochuko.tabsplit.data.api


import com.ochuko.tabsplit.models.User
import retrofit2.Response
import retrofit2.http.*


data class LoginRequest(val email: String, val password: String)
data class  RegisterRequest(val email: String, val password: String)

interface AuthApi {
    @POST("/auth/login")
    suspend fun login(@Body credentials: LoginRequest): Response<User>

    @POST("/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<User>
}