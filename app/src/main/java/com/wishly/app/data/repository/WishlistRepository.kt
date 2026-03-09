package com.wishly.app.data.repository

import com.wishly.app.data.api.WishlistApi
import com.wishly.app.data.model.CreateGiftItemRequest
import com.wishly.app.data.model.CreateWishlistRequest
import com.wishly.app.data.model.GiftItem
import com.wishly.app.data.model.ReserveGiftRequest
import com.wishly.app.data.model.Wishlist
import com.wishly.app.util.Result

class WishlistRepository(private val wishlistApi: WishlistApi) {

    suspend fun getMyWishlists(): Result<List<Wishlist>> {
        return try {
            val response = wishlistApi.getMyWishlists()
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get wishlists")
        }
    }

    suspend fun getWishlistById(id: String): Result<Wishlist> {
        return try {
            val response = wishlistApi.getWishlistById(id)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get wishlist")
        }
    }

    suspend fun createWishlist(request: CreateWishlistRequest): Result<Wishlist> {
        return try {
            val response = wishlistApi.createWishlist(request)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create wishlist")
        }
    }

    suspend fun deleteWishlist(id: String): Result<Unit> {
        return try {
            wishlistApi.deleteWishlist(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete wishlist")
        }
    }

    suspend fun addGiftItem(wishlistId: String, request: CreateGiftItemRequest): Result<GiftItem> {
        return try {
            val response = wishlistApi.addGiftItem(wishlistId, request)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to add gift item")
        }
    }

    suspend fun getGiftItems(wishlistId: String): Result<List<GiftItem>> {
        return try {
            val response = wishlistApi.getGiftItems(wishlistId)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get gift items")
        }
    }

    suspend fun reserveGift(
        wishlistId: String,
        itemId: String,
        request: ReserveGiftRequest
    ): Result<GiftItem> {
        return try {
            val response = wishlistApi.reserveGift(wishlistId, itemId, request)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to reserve gift")
        }
    }

    suspend fun cancelReservation(wishlistId: String, itemId: String): Result<Unit> {
        return try {
            wishlistApi.cancelReservation(wishlistId, itemId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to cancel reservation")
        }
    }
}