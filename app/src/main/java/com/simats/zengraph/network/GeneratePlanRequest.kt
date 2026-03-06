package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class GeneratePlanRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("goal") val goal: String,
    @SerializedName("mood") val mood: String,
    @SerializedName("level") val level: String
)
