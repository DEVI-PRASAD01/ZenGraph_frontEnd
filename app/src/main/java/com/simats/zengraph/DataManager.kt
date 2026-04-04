package com.simats.zengraph

import android.content.Context
import android.content.SharedPreferences

data class HistoryItem(
    val startTime: String,
    val endTime: String,
    val savedTime: String,
    val goal: String,
    val mood: String,
    val level: String,
    val suggestedMeditation: String
)

class DataManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ZenGraph", Context.MODE_PRIVATE)
    private val gson = com.google.gson.Gson()

    companion object {
        private const val KEY_STREAK = "streak"
        private const val KEY_TOTAL_MINUTES = "total_minutes"
        private const val KEY_XP = "xp"
        private const val KEY_LEVEL = "level"
        private const val KEY_PLAN_DAY = "plan_day"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_PLAN_ID = "plan_id"
        private const val KEY_SESSION_ID = "session_id"
        private const val KEY_PROFILE_COMPLETED = "profile_complete"
        private const val KEY_SELECTED_GOAL = "selected_goal"
        private const val KEY_SELECTED_MOOD = "selected_mood"
        private const val KEY_SELECTED_LEVEL = "selected_level"
        private const val KEY_PREDICTED_EMOTION = "predicted_emotion"
        private const val KEY_LAST_DURATION = "last_duration"
        private const val KEY_SESSION_NAME = "session_name"
        private const val KEY_HISTORY = "meditation_history"
    }

    var history: List<HistoryItem>
        get() {
            val json = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
            val type = object : com.google.gson.reflect.TypeToken<List<HistoryItem>>() {}.type
            return gson.fromJson(json, type)
        }
        set(value) {
            val json = gson.toJson(value)
            prefs.edit().putString(KEY_HISTORY, json).apply()
        }

    fun saveHistoryItem(item: HistoryItem) {
        val currentHistory = history.toMutableList()
        currentHistory.add(0, item) // Add to the beginning (latest first)
        history = currentHistory
    }

    var streak: Int
        get() = prefs.getInt(KEY_STREAK, 0)
        set(value) = prefs.edit().putInt(KEY_STREAK, value).apply()

    var totalMinutes: Int
        get() = prefs.getInt(KEY_TOTAL_MINUTES, 0)
        set(value) = prefs.edit().putInt(KEY_TOTAL_MINUTES, value).apply()

    var xp: Int
        get() = prefs.getInt(KEY_XP, 0)
        set(value) = prefs.edit().putInt(KEY_XP, value).apply()

    var level: String
        get() = prefs.getString(KEY_LEVEL, "Explorer") ?: "Explorer"
        set(value) = prefs.edit().putString(KEY_LEVEL, value).apply()

    var currentPlanDay: Int
        get() = prefs.getInt(KEY_PLAN_DAY, 1)
        set(value) = prefs.edit().putInt(KEY_PLAN_DAY, value).apply()

    var chatHistory: String
        get() = prefs.getString("chat_history", "[]") ?: "[]"
        set(value) = prefs.edit().putString("chat_history", value).apply()

    var currentUserId: Int
        get() = prefs.getInt(KEY_USER_ID, -1)
        set(value) = prefs.edit().putInt(KEY_USER_ID, value).apply()

    fun getUserId(): Int = currentUserId

    var planId: Int
        get() = prefs.getInt(KEY_PLAN_ID, -1)
        set(value) { prefs.edit().putInt(KEY_PLAN_ID, value).commit() }

    var sessionId: Int
        get() = prefs.getInt(KEY_SESSION_ID, -1)
        set(value) { prefs.edit().putInt(KEY_SESSION_ID, value).commit() }

    var profileCompleted: Boolean
        get() = prefs.getBoolean(KEY_PROFILE_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_PROFILE_COMPLETED, value).apply()

    var goal: String
        get() = prefs.getString(KEY_SELECTED_GOAL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_SELECTED_GOAL, value).apply()

    var mood: String
        get() = prefs.getString(KEY_SELECTED_MOOD, "") ?: ""
        set(value) = prefs.edit().putString(KEY_SELECTED_MOOD, value).apply()

    var selectedLevel: String
        get() = prefs.getString(KEY_SELECTED_LEVEL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_SELECTED_LEVEL, value).apply()

    var predictedEmotion: String
        get() = prefs.getString(KEY_PREDICTED_EMOTION, "") ?: ""
        set(value) = prefs.edit().putString(KEY_PREDICTED_EMOTION, value).apply()

    var lastDuration: Int
        get() = prefs.getInt(KEY_LAST_DURATION, 0)
        set(value) = prefs.edit().putInt(KEY_LAST_DURATION, value).apply()

    var sessionName: String
        get() = prefs.getString(KEY_SESSION_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_SESSION_NAME, value).apply()

    fun clearSession() {
        prefs.edit().apply {
            remove(KEY_PLAN_ID)
            remove(KEY_SESSION_ID)
            apply()
        }
    }

    fun addXp(amount: Int) {
        val newXp = xp + amount
        xp = newXp
        updateLevel(newXp)
    }

    fun completeSession(minutes: Int) {
        totalMinutes += minutes
        addXp(minutes * 10) // 10 XP per minute
        streak += 1 // Simplified: increment streak on every session for now
    }

    fun logSessionResult(stressReduced: String, calmLevel: String, focusImprovement: String) {
        prefs.edit().apply {
            putString("last_stress_reduced", stressReduced)
            putString("last_calm_level", calmLevel)
            putString("last_focus_improvement", focusImprovement)
            apply()
        }
    }

    private fun updateLevel(xp: Int) {
        level = when {
            xp >= 5000 -> "Master"
            xp >= 3000 -> "Expert"
            xp >= 1500 -> "Practitioner"
            xp >= 500 -> "Apprentice"
            else -> "Explorer"
        }
    }
}
