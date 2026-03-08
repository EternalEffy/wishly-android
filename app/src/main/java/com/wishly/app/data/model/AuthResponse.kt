package com.wishly.app.data.model

data class AuthResponse(
    val tokens: Tokens,
    val email: String,
    val id: String
)

data class Tokens(
    val accessToken: String,
    val refreshToken: String
)