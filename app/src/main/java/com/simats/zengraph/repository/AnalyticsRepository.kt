package com.simats.zengraph.repository

import com.simats.zengraph.network.*

class AnalyticsRepository(private val apiService: ApiService) {
    suspend fun getProgress(userId: Int, period: String = "day"): ProgressResponse {
        return apiService.getProgress(userId, period)
    }

    suspend fun getMoodTrend(userId: Int): MoodTrendResponse {
        return apiService.getMoodTrend(userId)
    }

    suspend fun getWeeklyCompletion(userId: Int): WeeklyCompletionResponse {
        val response = apiService.getWeeklyCompletion(userId)
        return if (response.isSuccessful) response.body()!! else throw Exception("Failed to load weekly completion")
    }

    suspend fun getSummary(userId: Int): SummaryResponse {
        return apiService.getSummary(userId)
    }

    suspend fun getEmotionTrend(userId: Int, period: String = "week"): EmotionTrendResponse {
        return apiService.getEmotionTrend(userId, period)
    }
}
