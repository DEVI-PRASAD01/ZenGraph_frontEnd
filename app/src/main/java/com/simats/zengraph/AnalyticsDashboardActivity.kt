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
        userId = dataManager.userId

        if (userId == -1) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupTabs()
        setupObservers()

        // Fetch real data for "Day" tab by default
        viewModel.loadAnalytics(userId, "day")

        binding.btnDetailedInsights.setOnClickListener {
            startActivity(Intent(this, PersonalizedInsightsActivity::class.java))
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.progressState.observe(this) { state ->
            when (state) {
                is AnalyticsState.Loading -> {
                    // Could show a shimmer or spinner here
                }
                is AnalyticsState.Success -> {
                    val data = state.data
                    binding.calmProgress.progress = data.calmScore
                    binding.tvCalmScore.text = data.calmScore.toString()
                    binding.tvMindfulMins.text = data.mindfulMinutes.toString()
                    binding.tvStressReduced.text = "${data.stressReduced}%"
                }
                is AnalyticsState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
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
                viewModel.loadProgress(userId, period)
            }
        }
    }
}
