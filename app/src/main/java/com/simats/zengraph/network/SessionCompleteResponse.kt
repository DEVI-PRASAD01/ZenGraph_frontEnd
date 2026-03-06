package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class SessionCompleteResponse(
    @SerializedName("duration_minutes") val durationMinutes: Int = 0,
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null
)
