package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class SessionStartRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("plan_id") val planId: Int
)
