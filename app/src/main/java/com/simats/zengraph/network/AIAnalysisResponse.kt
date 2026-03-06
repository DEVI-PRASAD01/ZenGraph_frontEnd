package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class AIAnalysisResponse(
    @SerializedName("stress_reduction") val stressReduction: String = "",
    @SerializedName("calm_score") val calmScore: Int = 0,
    @SerializedName("focus_improvement") val focusImprovement: String = "",
    @SerializedName("insight") val insight: String = ""
)
