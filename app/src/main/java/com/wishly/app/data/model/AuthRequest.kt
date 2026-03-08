package com.wishly.app.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String
)

data class RefreshRequest(
    val refreshToken: String
)