package com.simats.zengraph.repository

import com.simats.zengraph.network.*

class SessionRepository(private val apiService: ApiService) {
    suspend fun startSession(request: SessionStartRequest): SessionStartResponse {
        return apiService.startSession(request)
    }

    suspend fun completeSession(request: SessionCompleteRequest): SessionCompleteResponse {
        return apiService.completeSession(request)
    }

    suspend fun getSessionStats(userId: Int): SessionStatsResponse {
        return apiService.getSessionStats(userId)
    }

    suspend fun analyzeSession(request: AIAnalysisRequest): AIAnalysisResponse {
        return apiService.analyzeSession(request)
    }
}
