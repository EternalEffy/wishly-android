package com.wishly.app.presentation.auth

import androidx.lifecycle.ViewModel
import com.wishly.app.data.api.AuthApi

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val userId: String? = null,
    val userEmail: String? = null
)

class AuthViewModel(private val authApi: AuthApi) : ViewModel() {

}