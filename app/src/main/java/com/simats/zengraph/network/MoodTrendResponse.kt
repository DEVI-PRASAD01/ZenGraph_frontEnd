package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class MoodTrendItem(
    @SerializedName("date") val date: String,
    @SerializedName("score") val score: Int
)

typealias MoodTrendResponse = List<MoodTrendItem>
