package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class SummaryResponse(
    @SerializedName("total_sessions") val totalSessions: Int,
    @SerializedName("avg_mood_score") val avgMoodScore: Double,
    @SerializedName("completion_rate") val completionRate: Double
)
