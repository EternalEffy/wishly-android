package com.wishly.app.data.model

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("timestamp") val timestamp: String? = null,
    @SerializedName("path") val path: String? = null,
    @SerializedName("status") val status: Int? = null,
    @SerializedName("error") val error: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("requestId") val requestId: String? = null

)