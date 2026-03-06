package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    @SerializedName("total_sessions") val totalSessions: Int = 0,
    @SerializedName("total_hours") val totalHours: Float = 0f,
    @SerializedName("level") val level: Int = 0,
    @SerializedName("streak") val streak: Int = 0,
    @SerializedName("profile_completed") val profileCompleted: Boolean = false,
    @SerializedName("graph_data") val graphData: List<Float>? = emptyList(),
    @SerializedName("weekly_progress") val weeklyProgress: List<Float>? = emptyList()
)
