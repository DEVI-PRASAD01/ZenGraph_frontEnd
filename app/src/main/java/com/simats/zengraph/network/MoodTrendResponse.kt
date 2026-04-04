package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class MoodTrendItem(
    @SerializedName("date") val date: String,
    @SerializedName("score") val score: Float
)

data class MoodTrendResponse(
    @SerializedName("status") val status: String,
    @SerializedName("trend") val trend: List<MoodTrendItem>
)
