package com.simats.zengraph

/**
 * Global in-memory singleton for session state.
 * Guarantees session_id and duration survive activity navigation within the same app session.
 */
object SessionManager {
    var currentSessionId: Int? = null
    var currentPlanId: Int? = null
    var sessionDurationMinutes: Int = 15
    var lastDurationMinutes: Int? = null

    fun clearSession() {
        currentSessionId = null
        lastDurationMinutes = null
    }

    fun hasActiveSession(): Boolean {
        return currentSessionId != null && currentSessionId != -1
    }
}
