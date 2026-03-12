package com.wishly.app.data.api

import android.util.Log
import com.wishly.app.data.model.RefreshRequest
import com.wishly.app.data.model.Tokens
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class AuthInterceptor(
    private val tokenManager: TokenManager,
    private val baseUrl: String
) : Interceptor {

    private val refreshClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    private val refreshLock = ReentrantLock()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        Log.d(
            "AuthInterceptor",
            "=== INTERCEPT: ${originalRequest.method} ${originalRequest.url} ==="
        )

        if (isAuthEndpoint(originalRequest.url.toString())) {
            Log.d("AuthInterceptor", "Auth endpoint, skipping")
            return chain.proceed(originalRequest)
        }

        val accessToken = tokenManager.getAccessToken()
        Log.d(
            "AuthInterceptor",
            "Access token from cache: ${if (accessToken != null) "present" else "NULL"}"
        )
        val request = addAuthToken(originalRequest, accessToken)
        var response = chain.proceed(request)

        if (response.code == 401) {
            Log.d("AuthInterceptor", "401 detected, attempting refresh")
            response.close()

            val refreshToken = tokenManager.getRefreshToken()
            Log.d(
                "AuthInterceptor",
                "Refresh token from cache: ${if (refreshToken != null) "present" else "NULL/EMPTY"}"
            )
            if (!refreshToken.isNullOrEmpty()) {
                try {
                    Log.d("AuthInterceptor", "Calling refreshTokensSync()")
                    refreshLock.withLock {
                        val newTokens = refreshTokensSync(refreshToken)
                        Log.d("AuthInterceptor", "Refresh successful, saving tokens")
                        tokenManager.saveTokens(
                            accessToken = newTokens.accessToken,
                            refreshToken = newTokens.refreshToken
                        )
                    }

                    val newRequest = addAuthToken(originalRequest, tokenManager.getAccessToken())
                    Log.d("AuthInterceptor", "Retrying request with new token")
                    return chain.proceed(newRequest)

                } catch (e: Exception) {
                    Log.e("AuthInterceptor", "Refresh FAILED: ${e.message}", e)
                    tokenManager.clearTokens()
                    throw IOException("Authentication failed: ${e.message}", e)
                }
            }
        } else {
            Log.e("AuthInterceptor", "No refresh token available")
        }

        return response
    }

    private fun refreshTokensSync(refreshToken: String): Tokens {
        Log.d("AuthInterceptor", "refreshTokensSync() called")

        val json = JSONObject()
            .put("refreshToken", refreshToken)
            .toString()

        Log.d("AuthInterceptor", "Refresh request JSON: $json")

        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("${baseUrl}api/auth/refresh")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        Log.d("AuthInterceptor", "Executing refresh request to: ${baseUrl}api/auth/refresh")

        val response = refreshClient.newCall(request).execute()

        Log.d("AuthInterceptor", "Refresh response code: ${response.code}")

        if (!response.isSuccessful) {
            val errorBody = response.body?.string()
            Log.e("AuthInterceptor", "Refresh failed: ${response.code} - $errorBody")
            throw IOException("Refresh failed: ${response.code} - $errorBody")
        }

        val responseBody = response.body?.string()
            ?: throw IOException("Empty response body from refresh")

        val jsonResult = JSONObject(responseBody)

        val (accessToken, newRefreshToken) = if (jsonResult.has("tokens")) {
            val tokensObj = jsonResult.getJSONObject("tokens")
            tokensObj.getString("accessToken") to tokensObj.getString("refreshToken")
        } else {
            jsonResult.getString("accessToken") to jsonResult.getString("refreshToken")
        }

        response.close()
        Log.d("AuthInterceptor", "Refresh tokens extracted successfully")
        return Tokens(accessToken, newRefreshToken)
    }

    private fun addAuthToken(request: Request, token: String?): Request {
        return if (!token.isNullOrEmpty()) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }
    }

    private fun isAuthEndpoint(url: String): Boolean {
        return url.contains("/api/auth/login") ||
                url.contains("/api/auth/register") ||
                url.contains("/api/auth/refresh")
    }

    fun cleanup() {
        refreshClient.dispatcher.executorService.shutdown()
        refreshClient.connectionPool.evictAll()
    }
}