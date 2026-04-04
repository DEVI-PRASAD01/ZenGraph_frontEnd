package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class MoodHistoryResponse(
    @SerializedName("status") val status: String,
    @SerializedName("history") val history: List<MoodHistoryItem>
)

data class MoodHistoryItem(
    @SerializedName("mood") val mood: String,
    @SerializedName("date") val date: String
)
