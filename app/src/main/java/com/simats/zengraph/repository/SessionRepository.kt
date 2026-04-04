package com.simats.zengraph.repository

import com.simats.zengraph.network.*

class SessionRepository(private val apiService: ApiService) {
    suspend fun startSession(request: SessionStartRequest): SessionStartResponse {
        return apiService.startSession(request)
    }

    suspend fun completeSession(sessionId: Int, request: SessionCompleteRequest): SessionCompleteResponse {
        return apiService.completeSession(sessionId, request)
    }

    suspend fun getSessionStats(userId: Int): SessionStatsResponse {
        val response = apiService.getSessionStats(userId)
        return if (response.isSuccessful) response.body()!! else throw Exception("Failed to load session stats")
    }

    suspend fun analyzeSession(request: AIAnalysisRequest): AIAnalysisResponse {
        return apiService.analyzeSession(request)
    }
}
