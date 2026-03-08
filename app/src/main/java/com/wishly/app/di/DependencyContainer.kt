package com.wishly.app.di

import android.content.Context
import com.wishly.app.data.api.ApiClient
import com.wishly.app.data.api.AuthApi
import com.wishly.app.data.api.TokenManager
import com.wishly.app.data.api.WishlistApi
import com.wishly.app.data.repository.AuthRepository
import com.wishly.app.data.repository.WishlistRepository
import retrofit2.Retrofit

object DependencyContainer {
    private var tokenManager: TokenManager? = null
    private var retrofit: Retrofit? = null
    private var authApi: AuthApi? = null
    private var wishlistApi: WishlistApi? = null
    private var authRepository: AuthRepository? = null
    private var wishlistRepository: WishlistRepository? = null


    fun init(context: Context) {
        tokenManager = TokenManager(context)
        retrofit = ApiClient.create(context, tokenManager!!)
        authApi = retrofit!!.create(AuthApi::class.java)
        wishlistApi = retrofit!!.create(WishlistApi::class.java)
        authRepository = AuthRepository(authApi!!)
        wishlistRepository = WishlistRepository(wishlistApi!!)
    }

    fun getAuthRepository(): AuthRepository {
        return authRepository ?: throw IllegalStateException("Dependency Container not initialized")
    }

    fun getWishlistRepository(): WishlistRepository {
        return wishlistRepository
            ?: throw IllegalStateException("Dependency Container not initialized")
    }

    fun getTokenManager(): TokenManager {
        return tokenManager ?: throw IllegalStateException("DependencyContainer not initialized")
    }

}