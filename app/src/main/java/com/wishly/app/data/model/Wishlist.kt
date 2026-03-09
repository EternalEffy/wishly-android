package com.wishly.app.data.model

import com.google.gson.annotations.SerializedName

data class Wishlist(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("privacy") val privacy: String = "PUBLIC",
    @SerializedName("eventDate") val eventDate: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("items") val items: List<GiftItem> = emptyList()
)

data class GiftItem(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("productUrl") val productUrl: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("priority") val priority: String = "MEDIUM",
    @SerializedName("reserved") val reserved: Boolean = false,
    @SerializedName("reservedByName") val reservedByName: String? = null,
    @SerializedName("reservedAt") val reservedAt: String? = null
)

data class CreateWishlistRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("privacy") val privacy: String = "PUBLIC",
    @SerializedName("eventDate") val eventDate: String? = null
)

data class CreateGiftItemRequest(
    @SerializedName("name") val name: String,
    @SerializedName("productUrl") val productUrl: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("priority") val priority: String? = "MEDIUM"
)

data class ReserveGiftRequest(
    @SerializedName("guestName") val guestName: String,
    @SerializedName("guestEmail") val guestEmail: String
)

