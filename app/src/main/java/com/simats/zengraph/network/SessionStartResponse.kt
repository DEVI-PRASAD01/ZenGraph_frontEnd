package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class SessionStartResponse(
    @SerializedName("session_id") val sessionId: Int = -1,
    @SerializedName("session_name") val sessionName: String? = null,
    @SerializedName("duration") val duration: Int = 15,
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null
)
