package com.wishly.app.di

import com.wishly.app.presentation.auth.AuthViewModel
import com.wishly.app.presentation.create.CreateWishlistViewModel
import com.wishly.app.presentation.home.HomeViewModel

object ViewModelFactory {
    fun createAuthViewModel(): AuthViewModel {
        return AuthViewModel(authRepository = DependencyContainer.getAuthRepository())
    }

    fun getWishlistRepository() = DependencyContainer.getWishlistRepository()

    fun createHomeViewModel(): HomeViewModel {
        return HomeViewModel(
            wishlistRepository = DependencyContainer.getWishlistRepository()
        )
    }

    fun createCreateWishlistViewModel(): CreateWishlistViewModel {
        return CreateWishlistViewModel(
            wishlistRepository = DependencyContainer.getWishlistRepository()
        )
    }
}