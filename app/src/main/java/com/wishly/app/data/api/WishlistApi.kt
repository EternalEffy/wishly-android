package com.wishly.app.data.api

import com.wishly.app.data.model.CreateGiftItemRequest
import com.wishly.app.data.model.CreateWishlistRequest
import com.wishly.app.data.model.GiftItem
import com.wishly.app.data.model.ReserveGiftRequest
import com.wishly.app.data.model.Wishlist
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface WishlistApi {
    @GET("api/wishlists/user/me")
    suspend fun getMyWishlists(): Response<List<Wishlist>>

    @GET("api/wishlists/{id}")
    suspend fun getWishlistById(@Path("id") id: String): Response<Wishlist>

    @POST("api/wishlists")
    suspend fun createWishlist(@Body wishlist: CreateWishlistRequest): Response<Wishlist>

    @PUT("api/wishlists/{id}")
    suspend fun updateWishlist(
        @Path("id") id: String,
        @Body wishlist: CreateWishlistRequest
    ): Wishlist

    @DELETE("api/wishlists/{id}")
    suspend fun deleteWishlist(@Path("id") id: String): Response<Unit>

    @POST("api/wishlists/{id}/items")
    suspend fun addGiftItem(
        @Path("id") wishlistId: String,
        @Body item: CreateGiftItemRequest
    ): Response<GiftItem>

    @GET("api/wishlists/{id}/items")
    suspend fun getGiftItems(@Path("id") wishlistId: String): Response<List<GiftItem>>

    @PUT("api/wishlists/{id}/items/{itemId}")
    suspend fun updateGiftItem(
        @Path("id") wishlistId: String,
        @Path("itemId") itemId: String,
        @Body item: CreateGiftItemRequest
    ): GiftItem

    @DELETE("api/wishlists/{id}/items/{itemId}")
    suspend fun deleteGiftItem(
        @Path("id") wishlistId: String,
        @Path("itemId") itemId: String
    )

    @POST("api/wishlists/{id}/items/{itemId}/reserve")
    suspend fun reserveGift(
        @Path("id") wishlistId: String,
        @Path("itemId") itemId: String,
        @Body request: ReserveGiftRequest
    ): Response<GiftItem>

    @DELETE("api/wishlists/{id}/items/{itemId}/reserve")
    suspend fun cancelReservation(
        @Path("id") wishlistId: String,
        @Path("itemId") itemId: String
    ): Response<Unit>
}