package com.wishly.app.util

object Constants {
    const val BASE_URL = "https://wishly.eternaleffy.ru/"

    // API Endpoints
    const val API_AUTH = "api/auth/"
    const val API_WISHLISTS = "api/wishlists/"
    const val API_NOTES = "api/notes/"

    // DataStore keys
    const val ACCESS_TOKEN = "access_token"
    const val REFRESH_TOKEN = "refresh_token"
    const val USER_ID = "user_id"
    const val USER_EMAIL = "user_email"

    // Timeouts
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
}