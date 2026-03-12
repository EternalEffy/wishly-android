package com.wishly.app.data.api

import com.wishly.app.util.AppError
import com.wishly.app.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

object ApiErrorHandler {
    suspend fun <T> handleResponse(
        apiCall: suspend () -> Response<T>
    ): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Result.success(body)
                    } else {
                        Result.error(AppError.ServerError)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val appError = AppError.fromHttpCode(response.code(), errorBody)
                    Result.error(appError)
                }

            } catch (e: IOException) {
                Result.error(AppError.fromException(e))
            } catch (e: Exception) {
                Result.error(AppError.fromException(e))
            }
        }
    }

    suspend fun handleUnitResponse(
        apiCall: suspend () -> Response<Unit>
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()

                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val appError = AppError.fromHttpCode(response.code(), errorBody)
                    Result.error(appError)
                }

            } catch (e: IOException) {
                Result.error(AppError.fromException(e))
            } catch (e: Exception) {
                Result.error(AppError.fromException(e))
            }
        }
    }

    private fun parseErrorBody(errorBody: String?): AppError {
        return try {
            if (errorBody.isNullOrBlank()) return AppError.ServerError

            when {
                errorBody.contains("Unauthorized", ignoreCase = true) -> AppError.Unauthorized
                errorBody.contains("Forbidden", ignoreCase = true) -> AppError.Forbidden
                errorBody.contains("not found", ignoreCase = true) -> AppError.NotFound
                errorBody.contains("validation", ignoreCase = true) -> {
                    val details = extractValidationErrorDetails(errorBody)
                    AppError.ValidationError("request", details)
                }
                else -> AppError.ServerError
            }
        } catch (e: Exception) {
            AppError.Unknown("Failed to parse error: ${e.message}")
        }
    }

    private fun extractValidationErrorDetails(json: String): String {
        return try {
            val regex = """"(message|error)"\s*:\s*"([^"]+)"""".toRegex()
            regex.find(json)?.groupValues?.getOrNull(2) ?: "Validation error"
        } catch (e: Exception) {
            "Validation error"
        }
    }
}