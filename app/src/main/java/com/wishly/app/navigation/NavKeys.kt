package com.wishly.app.navigation

import com.google.gson.Gson
import com.wishly.app.data.model.Wishlist

object NavKeys {
    const val KEY_NEW_WISHLIST = "new_wishlist"

    fun wishlistToArg(wishlist: Wishlist): String {
        return Gson().toJson(wishlist)
    }

    fun argToWishlist(json: String): Wishlist {
        return Gson().fromJson(json, Wishlist::class.java)
    }
}