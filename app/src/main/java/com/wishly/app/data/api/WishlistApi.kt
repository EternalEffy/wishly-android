package com.wishly.app.data.api

import com.wishly.app.data.model.Wishlist

interface WishlistApi {

    suspend fun createWishlist(): Wishlist

    suspend fun getMyWishLists(): List<Wishlist>

    suspend fun getWishlist(): Wishlist

    suspend fun deleteWishlist()
}