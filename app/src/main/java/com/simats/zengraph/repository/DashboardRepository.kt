package com.simats.zengraph.repository

import com.simats.zengraph.network.*

class DashboardRepository(private val apiService: ApiService) {

    suspend fun getDashboard(userId: Int) = apiService.getDashboard(userId)

    suspend fun completeSession(sessionId: Int, request: SessionCompleteRequest) = 
        apiService.completeSession(sessionId, request)

    suspend fun checkIn(request: CheckInRequest) = 
        apiService.checkIn(request)

    suspend fun getSessionStats(userId: Int): SessionStatsResponse {
        val response = apiService.getSessionStats(userId)
        return if (response.isSuccessful) response.body()!! else throw Exception("Failed to load session stats")
    }

    suspend fun startSession(request: SessionStartRequest) =
        apiService.startSession(request)
}
