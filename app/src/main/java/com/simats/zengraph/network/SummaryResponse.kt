package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class SummaryResponse(
    @SerializedName("status") val status: String,
    @SerializedName("total_sessions") val totalSessions: Int,
    @SerializedName("total_minutes") val totalMinutes: Int,
    @SerializedName("calm_score") val calmScore: Int,
    @SerializedName("stress_reduced") val stressReduced: Int
)
