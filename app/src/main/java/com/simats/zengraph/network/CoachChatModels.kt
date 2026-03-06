package com.simats.zengraph.network

import com.google.gson.annotations.SerializedName

data class CoachChatRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("message") val message: String
)

data class CoachChatResponse(
    @SerializedName("emotion") val emotion: String? = null,
    @SerializedName("confidence") val confidence: Float = 0f,
    @SerializedName("reply") val reply: String = "",
    @SerializedName("recommended_session") val recommendedSession: RecommendedSession? = null
)

data class RecommendedSession(
    @SerializedName("title") val title: String = "",
    @SerializedName("duration") val duration: Int = 5,
    @SerializedName("category") val category: String = "Quick Calm"
)
