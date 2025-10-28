package com.partum.tabsplit.data.api


import com.partum.tabsplit.data.model.User
import retrofit2.Response
import retrofit2.http.*


data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val email: String, val password: String)
data class AuthResponse(val user: User, val token: String)
data class UpdateProfileRequest(val username: String?, val email: String?, val zaddr: String?)
data class UpdateProfile(
    val id: String?,
    val email: String?,
    val username: String?,
    val zaddr: String?,
)

data class UpdateProfileResponse(
    val user: UpdateProfile,
)

interface AuthApi {
    @POST("/auth/login")
    suspend fun login(@Body credentials: LoginRequest): Response<AuthResponse>

    @POST("/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @PUT("/auth/update-profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UpdateProfileResponse>
}