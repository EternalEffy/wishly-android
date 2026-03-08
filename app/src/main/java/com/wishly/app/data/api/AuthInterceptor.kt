package com.wishly.app.data.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val url = originalRequest.url.toString()
        if (url.contains("/api/auth/login") ||
            url.contains("/api/auth/register") ||
            url.contains("/api/auth/refresh")
        ) {
            return chain.proceed(originalRequest)
        }

        val token = tokenManager.getAccessToken()

        val request = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(request)
    }
}
