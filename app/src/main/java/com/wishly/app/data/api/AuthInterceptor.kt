package com.wishly.app.data.api

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONObject
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class AuthInterceptor(
    private val tokenManager: TokenManager,
    private val baseUrl: String
) : Interceptor {
    private val refreshClient = OkHttpClient.Builder()
        .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val refreshLock = ReentrantLock()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.toString()

        if (isAuthEndpoint(url)) {
            return chain.proceed(originalRequest)
        }

        val request = addAuthToken(originalRequest, tokenManager.getAccessToken())

        var response = chain.proceed(request)

        if (response.code == 401) {
            response.close()

            val refreshToken = tokenManager.getRefreshToken()
            if (!refreshToken.isNullOrEmpty()) {
                try {
                    refreshLock.withLock {
                        if (tokenManager.getAccessToken().isNullOrEmpty()) {
                            val newTokens = refreshTokensSync(refreshToken)
                            tokenManager.saveTokens(
                                accessToken = newTokens.accessToken,
                                refreshToken = newTokens.refreshToken
                            )
                        }
                    }

                    val newRequest = addAuthToken(
                        originalRequest,
                        tokenManager.getAccessToken()
                    )
                    return chain.proceed(newRequest)

                } catch (e: Exception) {
                    tokenManager.clearTokens()
                    throw IOException("Authentication failed: ${e.message}", e)
                }
            }
        }

        return response
    }

    private fun refreshTokensSync(refreshToken: String): Tokens {
        val json = JSONObject()
            .put("refreshToken", refreshToken)
            .toString()

        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$baseUrl/api/auth/refresh")
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        val response = refreshClient.newCall(request).execute()

        if (!response.isSuccessful) {
            throw IOException("Refresh failed: ${response.code}")
        }

        val responseBody = response.body?.string() ?: throw IOException("Empty response")
        val jsonResult = JSONObject(responseBody)

        val tokens = if (jsonResult.has("tokens")) {
            val tokensObj = jsonResult.getJSONObject("tokens")
            Tokens(
                accessToken = tokensObj.getString("accessToken"),
                refreshToken = tokensObj.getString("refreshToken")
            )
        } else {
            Tokens(
                accessToken = jsonResult.getString("accessToken"),
                refreshToken = jsonResult.getString("refreshToken")
            )
        }

        response.close()
        return tokens
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

data class Tokens(val accessToken: String, val refreshToken: String)