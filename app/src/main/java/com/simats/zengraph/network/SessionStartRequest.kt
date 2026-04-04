package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class SessionStartRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("goal") val goal: String,
    @SerializedName("mood_before") val moodBefore: String,
    @SerializedName("experience_level") val experienceLevel: String,
    @SerializedName("session_name") val sessionName: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("techniques") val techniques: String,
    @SerializedName("match_score") val matchScore: Int
)
