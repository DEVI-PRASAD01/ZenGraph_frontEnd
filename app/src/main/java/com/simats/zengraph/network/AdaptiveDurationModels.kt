package com.simats.zengraph.network

data class AdaptiveDurationResponse(
    val status: String,
    val adaptive_duration: Int?,
    val average_actual_minutes: Float?,
    val sessions_analysed: Int?,
    val message: String?
)
