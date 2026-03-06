package com.simats.zengraph.repository

import com.simats.zengraph.network.*

class DashboardRepository(private val apiService: ApiService) {

    suspend fun getDashboard(userId: Int) = apiService.getDashboard(userId)

    suspend fun completeSession(sessionId: Int) = 
        apiService.completeSession(SessionCompleteRequest(sessionId))

    suspend fun checkIn(userId: Int, moodScore: Float) = 
        apiService.checkIn(CheckInRequest(userId, moodScore))

    suspend fun getSessionStats(userId: Int) = apiService.getSessionStats(userId)

    suspend fun startSession(userId: Int, planId: Int) =
        apiService.startSession(SessionStartRequest(userId, planId))
}
