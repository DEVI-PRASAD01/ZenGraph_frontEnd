package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class EmotionPredictionRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("mood") val mood: String,
    @SerializedName("thought") val thought: String
)
