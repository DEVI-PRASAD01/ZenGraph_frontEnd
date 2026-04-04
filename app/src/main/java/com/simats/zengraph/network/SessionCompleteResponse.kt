package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class SessionCompleteResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("analysis") val analysis: SessionAnalysis? = null,
    @SerializedName("ai_recommendation") val aiRecommendation: AiRecommendation? = null
)

data class SessionAnalysis(
    @SerializedName("stress_reduced") val stressReduced: String,
    @SerializedName("calm_level") val calmLevel: Int,
    @SerializedName("focus_improvement") val focusImprovement: String,
    @SerializedName("mood_before") val moodBefore: String?,
    @SerializedName("mood_after") val moodAfter: String,
    @SerializedName("minutes_completed") val durationMinutes: Int
)

data class AiRecommendation(
    @SerializedName("next_session") val nextSession: String,
    @SerializedName("duration_minutes") val durationMinutes: Int,
    @SerializedName("technique") val technique: String
)
