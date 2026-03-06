package com.simats.zengraph.repository

import com.simats.zengraph.network.*

class RecommendationRepository(private val apiService: ApiService) {

    suspend fun selectExperience(userId: Int, experienceLevel: String): SimpleResponse {
        return apiService.selectExperience(ExperienceSelectionRequest(userId, experienceLevel))
    }

    suspend fun getDashboard(userId: Int): DashboardResponse {
        return apiService.getDashboard(userId)
    }

    suspend fun predictEmotion(userId: Int, mood: String, thought: String): EmotionPredictionResponse {
        return apiService.predictEmotion(EmotionPredictionRequest(userId, mood, thought))
    }
}
