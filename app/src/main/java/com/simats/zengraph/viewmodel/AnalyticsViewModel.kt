package com.simats.zengraph.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.zengraph.network.EmotionTrendResponse
import com.simats.zengraph.network.MoodTrendResponse
import com.simats.zengraph.network.ProgressResponse
import com.simats.zengraph.network.SummaryResponse
import com.simats.zengraph.network.WeeklyCompletionResponse
import com.simats.zengraph.repository.AnalyticsRepository
import kotlinx.coroutines.launch

sealed class AnalyticsState<out T> {
    object Loading : AnalyticsState<Nothing>()
    data class Success<out T>(val data: T) : AnalyticsState<T>()
    data class Error(val message: String) : AnalyticsState<Nothing>()
}

class AnalyticsViewModel(private val repository: AnalyticsRepository) : ViewModel() {

    private val _progressState = MutableLiveData<AnalyticsState<ProgressResponse>>()
    val progressState: LiveData<AnalyticsState<ProgressResponse>> = _progressState

    private val _moodTrendState = MutableLiveData<AnalyticsState<MoodTrendResponse>>()
    val moodTrendState: LiveData<AnalyticsState<MoodTrendResponse>> = _moodTrendState

    private val _weeklyCompletionState = MutableLiveData<AnalyticsState<WeeklyCompletionResponse>>()
    val weeklyCompletionState: LiveData<AnalyticsState<WeeklyCompletionResponse>> = _weeklyCompletionState

    private val _summaryState = MutableLiveData<AnalyticsState<SummaryResponse>>()
    val summaryState: LiveData<AnalyticsState<SummaryResponse>> = _summaryState

    private val _emotionTrendState = MutableLiveData<AnalyticsState<EmotionTrendResponse>>()
    val emotionTrendState: LiveData<AnalyticsState<EmotionTrendResponse>> = _emotionTrendState

    /** Load all analytics for the given period (day, week, month) */
    fun loadAnalytics(userId: Int, period: String = "day") {
        loadProgress(userId, period)

        viewModelScope.launch {
            try {
                _moodTrendState.value = AnalyticsState.Success(repository.getMoodTrend(userId))
            } catch (e: Exception) {
                _moodTrendState.value = AnalyticsState.Error(e.localizedMessage ?: "Failed")
            }
        }
        viewModelScope.launch {
            try {
                _weeklyCompletionState.value = AnalyticsState.Success(repository.getWeeklyCompletion(userId))
            } catch (e: Exception) {
                _weeklyCompletionState.value = AnalyticsState.Error(e.localizedMessage ?: "Failed")
            }
        }
        viewModelScope.launch {
            try {
                _summaryState.value = AnalyticsState.Success(repository.getSummary(userId))
            } catch (e: Exception) {
                _summaryState.value = AnalyticsState.Error(e.localizedMessage ?: "Failed")
            }
        }
    }

    /** Load only progress for tab switching (day/week/month) */
    fun loadProgress(userId: Int, period: String = "day") {
        viewModelScope.launch {
            _progressState.value = AnalyticsState.Loading
            try {
                _progressState.value = AnalyticsState.Success(repository.getProgress(userId, period))
            } catch (e: Exception) {
                _progressState.value = AnalyticsState.Error(e.localizedMessage ?: "Failed to load progress")
            }
        }
    }

    /** Load emotion trend for radar chart */
    fun loadEmotionTrend(userId: Int, period: String = "week") {
        viewModelScope.launch {
            _emotionTrendState.value = AnalyticsState.Loading
            try {
                _emotionTrendState.value = AnalyticsState.Success(repository.getEmotionTrend(userId, period))
            } catch (e: Exception) {
                _emotionTrendState.value = AnalyticsState.Error(e.localizedMessage ?: "Failed to load emotion trend")
            }
        }
    }
}
