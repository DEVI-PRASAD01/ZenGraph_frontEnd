package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class ExperienceSelectionRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("experience_level") val experienceLevel: String
)
