package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class AIAnalysisRequest(
    @SerializedName("pre_emotion") val preEmotion: String,
    @SerializedName("post_mood") val postMood: String,
    @SerializedName("duration") val duration: Int
)
