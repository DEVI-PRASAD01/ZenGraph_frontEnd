package com.simats.zengraph.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.zengraph.network.DashboardResponse
import com.simats.zengraph.network.SessionStatsResponse
import com.simats.zengraph.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DashboardState {
    object Idle : DashboardState()
    object Loading : DashboardState()
    data class Success(val data: DashboardResponse) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

sealed class SessionStatsState {
    object Idle : SessionStatsState()
    object Loading : SessionStatsState()
    data class Success(val data: SessionStatsResponse) : SessionStatsState()
    data class Error(val message: String) : SessionStatsState()
}

sealed class ActionState {
    object Idle : ActionState()
    object Loading : ActionState()
    data class Success(val message: String) : ActionState()
    data class Error(val message: String) : ActionState()
}

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {

    private val _dashboardState = MutableStateFlow<DashboardState>(DashboardState.Idle)
    val dashboardState: StateFlow<DashboardState> = _dashboardState.asStateFlow()

    private val _sessionStatsState = MutableStateFlow<SessionStatsState>(SessionStatsState.Idle)
    val sessionStatsState: StateFlow<SessionStatsState> = _sessionStatsState.asStateFlow()

    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState.asStateFlow()

    private val _startSessionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val startSessionState: StateFlow<ActionState> = _startSessionState.asStateFlow()

    fun loadDashboard(userId: Int) {
        viewModelScope.launch {
            _dashboardState.value = DashboardState.Loading
            try {
                val response = repository.getDashboard(userId)
                _dashboardState.value = DashboardState.Success(response)
            } catch (e: Exception) {
                _dashboardState.value = DashboardState.Error(e.message ?: "Failed to load dashboard")
            }
        }
    }

    fun loadSessionStats(userId: Int) {
        viewModelScope.launch {
            _sessionStatsState.value = SessionStatsState.Loading
            try {
                val response = repository.getSessionStats(userId)
                _sessionStatsState.value = SessionStatsState.Success(response)
            } catch (e: Exception) {
                _sessionStatsState.value = SessionStatsState.Error(e.message ?: "Failed to load session stats")
            }
        }
    }

    fun startSession(userId: Int) {
        viewModelScope.launch {
            _startSessionState.value = ActionState.Loading
            try {
                // Use generic defaults for background starts if needed
                val request = com.simats.zengraph.network.SessionStartRequest(
                    userId          = userId,
                    goal            = "Daily Focus",
                    moodBefore      = "Neutral",
                    experienceLevel = "Beginner",
                    sessionName     = "Quick Focus",
                    duration        = 10,
                    techniques      = "Mindful Breathing",
                    matchScore      = 85
                )
                val response = repository.startSession(request)
                com.simats.zengraph.SessionManager.currentSessionId = response.sessionId
                com.simats.zengraph.SessionManager.sessionDurationMinutes = response.duration
                _startSessionState.value = ActionState.Success(response.status ?: "Success")
            } catch (e: Exception) {
                _startSessionState.value = ActionState.Error(e.message ?: "Failed to start session")
            }
        }
    }

    fun resetStartSessionState() {
        _startSessionState.value = ActionState.Idle
    }

    fun checkIn(userId: Int, moodScore: Float) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            try {
                val request = com.simats.zengraph.network.CheckInRequest(userId, moodScore)
                val response = repository.checkIn(request)
                _actionState.value = ActionState.Success(response.message ?: "Success")
            } catch (e: Exception) {
                _actionState.value = ActionState.Error(e.message ?: "Check-in failed")
            }
        }
    }

    fun resetActionState() {
        _actionState.value = ActionState.Idle
    }
}
