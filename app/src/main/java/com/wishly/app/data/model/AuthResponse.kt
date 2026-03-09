package com.wishly.app.data.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("accessToken") val accessToken: String? = null,
    @SerializedName("refreshToken") val refreshToken: String? = null,
    @SerializedName("tokens") val tokens: Tokens? = null,
    @SerializedName("email") val email: String,
    @SerializedName("id") val id: String
) {
    val safeAccessToken: String
        get() = accessToken ?: tokens?.accessToken ?: ""

    val safeRefreshToken: String
        get() = refreshToken ?: tokens?.refreshToken ?: ""
}

data class Tokens(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String
)