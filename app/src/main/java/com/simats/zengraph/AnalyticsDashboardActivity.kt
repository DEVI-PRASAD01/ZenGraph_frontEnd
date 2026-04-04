package com.simats.zengraph

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simats.zengraph.databinding.ActivityAnalyticsDashboardBinding
import androidx.activity.viewModels
import com.simats.zengraph.network.RetrofitClient
import com.simats.zengraph.repository.AnalyticsRepository
import com.simats.zengraph.viewmodel.AnalyticsState
import com.simats.zengraph.viewmodel.AnalyticsViewModel
import com.simats.zengraph.viewmodel.AnalyticsViewModelFactory
import android.widget.Toast
import android.view.animation.OvershootInterpolator
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.simats.zengraph.network.*

class AnalyticsDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalyticsDashboardBinding
    private val viewModel: AnalyticsViewModel by viewModels {
        AnalyticsViewModelFactory(AnalyticsRepository(RetrofitClient.apiService))
    }
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyticsDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataManager = DataManager(this)
        userId = dataManager.currentUserId

        if (userId == -1) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupTabs()
        // setupObservers() // Using direct loadAnalytics now

        // Fetch initial data
        loadAnalytics("day")


        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadAnalytics(period: String = "week") {
        lifecycleScope.launch {
            try {
                val apiService = RetrofitClient.apiService

                // Main progress data
                val progressResponse = apiService.getProgressAnalytics(userId, period)
                if (progressResponse.isSuccessful && progressResponse.body() != null) {
                    val data = progressResponse.body()!!
                    binding.calmProgress.progress = data.calmScore
                    binding.tvCalmScore.text = data.calmScore.toString()
                    binding.tvMindfulMins.text = data.mindfulMinutes.toString()
                    binding.tvStressReduced.text = "${data.stressReduced}%"
                }

                // Summary totals
                val summaryResponse = apiService.getAnalyticsSummary(userId)
                if (summaryResponse.isSuccessful && summaryResponse.body()?.status == "success") {
                    val summary = summaryResponse.body()!!
                    // Update summary views if they exist in binding
                    // binding.tvTotalSessions?.text = summary.totalSessions.toString()
                }

                // Weekly completion
                val weeklyResponse = apiService.getWeeklyCompletion(userId)
                if (weeklyResponse.isSuccessful && weeklyResponse.body()?.status == "success") {
                    val weekly = weeklyResponse.body()!!
                    // binding.tvWeeklyCount?.text = weekly.completedThisWeek.toString()
                }

            } catch (e: Exception) {
                Toast.makeText(this@AnalyticsDashboardActivity,
                    "Could not load analytics", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupTabs() {
        val tabs = listOf(binding.tabDay, binding.tabWeek, binding.tabMonth)

        tabs.forEach { tab ->
            tab.setOnClickListener { clickedTab ->
                tabs.forEach { t ->
                    if (t == clickedTab) {
                        t.setBackgroundResource(R.drawable.bg_goal_card_selected)
                        t.setTextColor(getColor(R.color.white))

                        t.animate()
                            .scaleX(0.9f)
                            .scaleY(0.9f)
                            .setDuration(100)
                            .withEndAction {
                                t.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(200)
                                    .setInterpolator(OvershootInterpolator())
                                    .start()
                            }
                            .start()
                    } else {
                        t.background = null
                        t.setTextColor(getColor(R.color.text_secondary))
                        t.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                    }
                }
                // Call API with the correct period
                val period = when (clickedTab.id) {
                    binding.tabDay.id -> "day"
                    binding.tabWeek.id -> "week"
                    binding.tabMonth.id -> "month"
                    else -> "day"
                }
                loadAnalytics(period)
            }
        }
    }
}
