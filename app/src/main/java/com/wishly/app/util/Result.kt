package com.wishly.app.util

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(
        val message: String,
        val code: Int? = null,
        val exception: Exception? = null
    ) : Result<Nothing>()

    object Loading : Result<Nothing>()

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    val isLoading: Boolean
        get() = this is Loading
}