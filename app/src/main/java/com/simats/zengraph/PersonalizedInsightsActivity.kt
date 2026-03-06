package com.simats.zengraph

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.simats.zengraph.databinding.ActivityPersonalizedInsightsBinding
import com.simats.zengraph.network.EmotionPredictionRequest
import com.simats.zengraph.network.RetrofitClient
import com.simats.zengraph.repository.SettingsRepository
import com.simats.zengraph.viewmodel.ProfileState
import com.simats.zengraph.viewmodel.SettingsViewModel
import com.simats.zengraph.viewmodel.SettingsViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PersonalizedInsightsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalizedInsightsBinding
    private var isProcessing = false

    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(SettingsRepository(RetrofitClient.apiService))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalizedInsightsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataManager = DataManager(this)
        val userId = dataManager.userId

        // Retrieve data passed from previous steps (Goal, Mood)
        val goal = intent.getStringExtra("EXTRA_GOAL") ?: dataManager.goal.ifEmpty { "Anxiety Relief" }
        val mood = intent.getStringExtra("EXTRA_MOOD") ?: dataManager.mood.ifEmpty { "Neutral" }

        updatePersonalization(goal, mood)
        observeProfilePhoto()

        if (userId != -1) {
            settingsViewModel.loadProfile(userId)
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.btnMaybeLater.setOnClickListener {
            finish()
        }

        binding.btnStartPlan.setOnClickListener {
            if (!isProcessing) {
                callPredictEmotion(dataManager, mood)
            }
        }
    }

    private fun observeProfilePhoto() {
        lifecycleScope.launchWhenStarted {
            settingsViewModel.profileState.collectLatest { state ->
                if (state is ProfileState.Success) {
                    val photoUrl = state.data.profileImage
                    if (!photoUrl.isNullOrBlank()) {
                        Glide.with(this@PersonalizedInsightsActivity)
                            .load(photoUrl)
                            .transform(CircleCrop())
                            .placeholder(R.drawable.ic_profile)
                            .into(binding.ivHeaderProfile)
                    }
                }
            }
        }
    }

    private fun callPredictEmotion(dataManager: DataManager, mood: String) {
        val userId = dataManager.userId
        if (userId == -1) {
            Toast.makeText(this, "User session expired. Please login again.", Toast.LENGTH_LONG).show()
            return
        }

        isProcessing = true
        binding.btnStartPlan.isEnabled = false

        val thought = dataManager.mood.ifEmpty { mood }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.predictEmotion(
                    EmotionPredictionRequest(userId, mood, thought)
                )

                // Save prediction result to DataManager
                dataManager.predictedEmotion = response.predictedEmotion

                // Navigate to AI Plan screen on success
                val intent = Intent(this@PersonalizedInsightsActivity, AiPlanActivity::class.java)
                intent.putExtras(this@PersonalizedInsightsActivity.intent)
                intent.putExtra("EXTRA_PREDICTED_EMOTION", response.predictedEmotion)
                intent.putExtra("EXTRA_CONFIDENCE", response.confidence)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@PersonalizedInsightsActivity,
                    e.message ?: "Failed to predict emotion",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                isProcessing = false
                binding.btnStartPlan.isEnabled = true
            }
        }
    }

    private fun updatePersonalization(goal: String, mood: String) {
        // Dynamic message based on mood in the new insight box
        val message = when (mood) {
            "Anxious", "Stressed" -> "Helps reduce stress & improves calm"
            "Tired", "Low" -> "Revitalizes your energy & focus"
            "Neutral", "Normal" -> "Maintains stability & clarity"
            "Happy", "Great", "Excited" -> "Amplifies your positive state"
            else -> "Perfectly balanced for your state"
        }
        binding.txtAdaptiveMessage.text = message

        // Dynamic session name based on goal
        val sessionName = when (goal) {
            "Anxiety Relief" -> "Oceanic\nCalm"
            "Better Sleep" -> "Midnight\nDrift"
            "Focus & Productivity" -> "Peak\nClarity"
            "Build Self-Esteem" -> "Soul\nStrength"
            else -> "Zen\nHarmony"
        }
        binding.txtSessionName.text = sessionName
    }
}
