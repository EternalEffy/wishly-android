package com.wishly.app.data.repository

import com.wishly.app.data.api.AuthApi
import com.wishly.app.data.api.TokenManager
import com.wishly.app.data.model.AuthResponse
import com.wishly.app.data.model.ErrorResponse
import com.wishly.app.data.model.LoginRequest
import com.wishly.app.data.model.RegisterRequest
import com.wishly.app.util.Result
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = authApi.login(LoginRequest(email, password))

            tokenManager.saveTokens(
                accessToken = response.safeAccessToken,
                refreshToken = response.safeRefreshToken
            )

            Result.Success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = parseErrorMessage(errorBody, e.code())

            Result.Error(message = errorMessage, code = e.code())
        } catch (e: SocketTimeoutException) {
            Result.Error(message = "Connection timeout.Please check your internet.", code = null)
        } catch (e: UnknownHostException) {
            Result.Error(message = "No internet connection. Please try again.", code = null)
        } catch (e: Exception) {
            Result.Error(message = e.message ?: "Login failed", code = null)
        }
    }

    suspend fun register(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = authApi.register(RegisterRequest(email, password))

            tokenManager.saveTokens(
                accessToken = response.safeAccessToken,
                refreshToken = response.safeRefreshToken
            )

            Result.Success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = parseErrorMessage(errorBody, e.code())

            Result.Error(message = errorMessage, code = e.code())
        } catch (e: SocketTimeoutException) {
            Result.Error(message = "Connection timeout.Please check your internet.", code = null)
        } catch (e: UnknownHostException) {
            Result.Error(message = "No internet connection. Please try again.", code = null)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Registration failed")
        }
    }

    private fun parseErrorMessage(errorBody: String?, statusCode: Int): String {
        return try {
            if (errorBody != null) {
                val errorResponse = com.google.gson.Gson().fromJson(
                    errorBody,
                    ErrorResponse::class.java
                )
                errorResponse.message ?: errorResponse.error ?: getStatusCodeMessage(statusCode)
            } else {
                getStatusCodeMessage(statusCode)
            }
        } catch (e: Exception) {
            getStatusCodeMessage(statusCode)
        }
    }

    private fun getStatusCodeMessage(code: Int): String {
        return when (code) {
            400 -> "Invalid request. Please check your input."
            401 -> "Invalid email or password."
            403 -> "Access denied."
            404 -> "Endpoint not found."
            409 -> "Email already registered."
            500 -> "Server error. Please try again later."
            502 -> "Server unavailable. Please try again later."
            503 -> "Service temporarily unavailable."
            else -> "Error: $code"
        }
    }

    suspend fun logout() {
        tokenManager.clearTokens()
    }

    fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()
}