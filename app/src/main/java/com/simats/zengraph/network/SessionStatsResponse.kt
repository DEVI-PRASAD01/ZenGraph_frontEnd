package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class SessionStatsResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("total_sessions") val totalSessions: Int = 0,
    @SerializedName("completed_sessions") val completedSessions: Int = 0,
    @SerializedName("total_minutes") val totalMinutes: Int = 0,
    @SerializedName("current_streak") val currentStreak: Int = 0,
    @SerializedName("level") val level: Int = 0
)
