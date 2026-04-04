package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class WeeklyCompletionResponse(
    @SerializedName("status") val status: String,
    @SerializedName("completed_this_week") val completedThisWeek: Int,
    @SerializedName("total_minutes") val totalMinutes: Int
)
