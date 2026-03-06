package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class SessionStatsResponse(
    @SerializedName("daily_streak") val dailyStreak: Int = 0,
    @SerializedName("level") val level: Int = 0,
    @SerializedName("weekly_hours") val weeklyHours: Float = 0f
)
