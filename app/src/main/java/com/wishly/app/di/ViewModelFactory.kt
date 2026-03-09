package com.wishly.app.di

import com.wishly.app.presentation.auth.AuthViewModel

object ViewModelFactory {
    fun createAuthViewModel(): AuthViewModel {
        return AuthViewModel(authRepository = DependencyContainer.getAuthRepository())
    }
}