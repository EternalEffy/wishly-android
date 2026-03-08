package com.wishly.app.data.model

data class Wishlist(
    val hash: String,
    val content: String,
    val privacy: String,
    val createdAt: String,
    val expiresAt: String?,
    val ownerId: String?
)

data class CreateWishlistRequest(
    val content: String,
    val privacy: String,
    val expiresAt: String? = null
)
