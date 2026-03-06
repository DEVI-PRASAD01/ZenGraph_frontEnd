package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class EmotionTrendItem(
    @SerializedName("date") val date: String,
    @SerializedName("score") val score: Int
)

typealias EmotionTrendResponse = List<EmotionTrendItem>
