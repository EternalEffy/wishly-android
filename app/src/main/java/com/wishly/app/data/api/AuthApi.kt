package com.wishly.app.data.api

import com.wishly.app.data.model.AuthResponse
import com.wishly.app.data.model.LoginRequest
import com.wishly.app.data.model.RefreshRequest
import com.wishly.app.data.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthApi {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/refresh")
    suspend fun refresh(@Body refreshToken: RefreshRequest): AuthResponse

}