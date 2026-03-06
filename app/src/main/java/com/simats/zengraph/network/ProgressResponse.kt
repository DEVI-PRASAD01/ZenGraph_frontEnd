package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class ProgressResponse(
    @SerializedName("calm_score") val calmScore: Int,
    @SerializedName("mindful_minutes") val mindfulMinutes: Int,
    @SerializedName("stress_reduced") val stressReduced: Int
)
