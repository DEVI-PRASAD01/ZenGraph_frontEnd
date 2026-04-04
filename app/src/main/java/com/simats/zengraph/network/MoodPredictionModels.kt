package com.simats.zengraph.network

data class MoodPredictionResponse(
    val status: String,
    val predicted_mood: String?,
    val recommended_session: PredictedSession?,
    val confidence: Float?,
    val message: String?
)

data class PredictedSession(
    val name: String,
    val duration: Int,
    val technique: String,
    val goal: String
)
