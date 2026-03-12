package com.wishly.app.util

sealed class AppError(
    open val message: String,
    val userMessage: String,
    val isCritical: Boolean = false,
    val allowRetry: Boolean = true
) {
    object Unauthorized : AppError(
        message = "Unauthorized",
        userMessage = "Session expired. Please log in again.",
        isCritical = true,
        allowRetry = false
    )

    object TokenExpired : AppError(
        message = "Token expired",
        userMessage = "Session expired. Please log in again.",
        isCritical = true,
        allowRetry = false
    )

    object NoInternet : AppError(
        message = "No internet connection",
        userMessage = "No internet connection. Check your connection.",
        isCritical = false,
        allowRetry = true
    )

    object Timeout : AppError(
        message = "Connection timeout",
        userMessage = "The server is not responding. Try again.",
        isCritical = false,
        allowRetry = true
    )

    object ServerError : AppError(
        message = "Internal server error",
        userMessage = "Something went wrong on the server. Please try again later.",
        isCritical = false,
        allowRetry = true
    )

    object NotFound : AppError(
        message = "Resource not found",
        userMessage = "The requested element was not found.",
        isCritical = true,
        allowRetry = false
    )

    data class ValidationError(
        val field: String,
        val details: String
    ) : AppError(
        message = "Validation failed: $field - $details",
        userMessage = details,
        isCritical = false,
        allowRetry = true
    )

    object BadRequest : AppError(
        message = "Bad request",
        userMessage = "Incorrect data. Please check the fields you filled in.",
        isCritical = false,
        allowRetry = true
    )

    object Forbidden : AppError(
        message = "Forbidden",
        userMessage = "You do not have access to this resource.",
        isCritical = true,
        allowRetry = false
    )

    data class Unknown(
        override val message: String
    ) : AppError(
        message = message,
        userMessage = "An error occurred: ${message.take(50)}",
        isCritical = false,
        allowRetry = true
    )

    companion object {
        fun fromHttpCode(code: Int, responseBody: String? = null): AppError {
            return when (code) {
                400 -> ValidationError("request", responseBody ?: "Incorrect data")
                401 -> Unauthorized
                403 -> Forbidden
                404 -> NotFound
                500, 502, 503 -> ServerError
                else -> Unknown("HTTP $code: ${responseBody ?: "Unknown error"}")
            }
        }

        fun fromException(e: Exception): AppError {
            return when {
                e.message?.contains("timeout", ignoreCase = true) == true -> Timeout
                e.message?.contains("unable to resolve host", ignoreCase = true) == true -> NoInternet
                e.message?.contains("connection", ignoreCase = true) == true -> NoInternet
                else -> Unknown(e.message ?: "Unknown error")
            }
        }
    }
}