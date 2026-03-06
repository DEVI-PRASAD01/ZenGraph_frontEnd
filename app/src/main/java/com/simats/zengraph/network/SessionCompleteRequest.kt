package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class SessionCompleteRequest(
    @SerializedName("session_id") val sessionId: Int
)
