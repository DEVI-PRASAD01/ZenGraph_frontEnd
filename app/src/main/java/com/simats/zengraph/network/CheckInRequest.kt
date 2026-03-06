package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class CheckInRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("mood_score") val moodScore: Float
)
