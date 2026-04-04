package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class AnalyticsSummaryResponse(
    @SerializedName("status") val status: String,
    @SerializedName("total_sessions") val totalSessions: Int,
    @SerializedName("total_minutes") val totalMinutes: Int,
    @SerializedName("calm_score") val calmScore: Int,
    @SerializedName("stress_reduced") val stressReduced: Int
)

data class ProgressAnalyticsResponse(
    @SerializedName("period") val period: String,
    @SerializedName("calm_score") val calmScore: Int,
    @SerializedName("mindful_minutes") val mindfulMinutes: Int,
    @SerializedName("stress_reduced") val stressReduced: Int
)
