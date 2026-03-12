package com.wishly.app.data.repository

import com.wishly.app.data.api.ApiErrorHandler
import com.wishly.app.data.api.WishlistApi
import com.wishly.app.data.model.CreateGiftItemRequest
import com.wishly.app.data.model.CreateWishlistRequest
import com.wishly.app.data.model.GiftItem
import com.wishly.app.data.model.ReserveGiftRequest
import com.wishly.app.data.model.Wishlist
import com.wishly.app.util.Result
class WishlistRepository(private val wishlistApi: WishlistApi) {

    suspend fun getMyWishlists(): Result<List<Wishlist>> {
        return ApiErrorHandler.handleResponse {
            wishlistApi.getMyWishlists()
        }
    }

    suspend fun getWishlistById(id: String): Result<Wishlist> {
        return ApiErrorHandler.handleResponse {
            wishlistApi.getWishlistById(id)
        }
    }


    suspend fun createWishlist(request: CreateWishlistRequest): Result<Wishlist> {
        return ApiErrorHandler.handleResponse {
            wishlistApi.createWishlist(request)
        }
    }

    suspend fun deleteWishlist(id: String): Result<Unit> {
        return ApiErrorHandler.handleUnitResponse {
            wishlistApi.deleteWishlist(id)
        }
    }

    suspend fun addGiftItem(wishlistId: String, request: CreateGiftItemRequest): Result<GiftItem> {
        return ApiErrorHandler.handleResponse {
            wishlistApi.addGiftItem(wishlistId, request)
        }
    }

    suspend fun getGiftItems(wishlistId: String): Result<List<GiftItem>> {
        return ApiErrorHandler.handleResponse {
            wishlistApi.getGiftItems(wishlistId)
        }
    }

    suspend fun reserveGift(
        wishlistId: String,
        itemId: String,
        request: ReserveGiftRequest
    ): Result<GiftItem> {
        return ApiErrorHandler.handleResponse {
            wishlistApi.reserveGift(wishlistId, itemId, request)
        }
    }

    suspend fun cancelReservation(wishlistId: String, itemId: String): Result<Unit> {
        return ApiErrorHandler.handleUnitResponse {
            wishlistApi.cancelReservation(wishlistId, itemId)
        }
    }
}