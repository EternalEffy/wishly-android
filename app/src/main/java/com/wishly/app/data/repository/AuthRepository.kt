package com.wishly.app.data.repository

import com.wishly.app.data.api.AuthApi
import com.wishly.app.data.model.AuthResponse
import com.wishly.app.data.model.LoginRequest
import com.wishly.app.data.model.RegisterRequest
import com.wishly.app.util.Result

class AuthRepository(private val authApi: AuthApi) {

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = authApi.login(LoginRequest(email, password))
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Login failed")
        }
    }

    suspend fun register(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = authApi.register(RegisterRequest(email, password))
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Registration failed")
        }
    }
}