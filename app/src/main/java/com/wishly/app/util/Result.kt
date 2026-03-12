package com.wishly.app.util

sealed class Result<out T> {

    data class Success<out T>(val data: T) : Result<T>()

    data class Error(
        val message: String,
        val code: Int? = null,
        val exception: Exception? = null,
        val appError: AppError? = null
    ) : Result<Nothing>()

    object Loading : Result<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading


    fun toAppError(): AppError {
        return when (this) {
            is Error -> {
                appError ?: when {
                    code != null -> AppError.fromHttpCode(code, message)
                    exception != null -> AppError.fromException(exception)
                    else -> AppError.Unknown(message)
                }
            }
            is Success, is Loading -> AppError.Unknown("Unexpected state: $this")
        }
    }

    fun getUserMessage(): String {
        return when (this) {
            is Error -> appError?.userMessage ?: message
            is Success -> ""
            is Loading -> "Loading..."
        }
    }

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun error(message: String, code: Int? = null, exception: Exception? = null): Result<Nothing> =
            Error(message, code, exception)
        fun error(appError: AppError): Result<Nothing> =
            Error(appError.message, appError = appError)
        fun loading(): Result<Nothing> = Loading
    }
}