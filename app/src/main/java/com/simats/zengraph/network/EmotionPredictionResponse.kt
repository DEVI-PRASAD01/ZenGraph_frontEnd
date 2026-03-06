package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class EmotionPredictionResponse(
    @SerializedName("predicted_emotion") val predictedEmotion: String,
    @SerializedName("confidence") val confidence: Float,
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val message: String? = null
)
