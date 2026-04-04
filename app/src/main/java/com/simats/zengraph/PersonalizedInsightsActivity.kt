package com.simats.zengraph

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
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
        val userId = dataManager.currentUserId

        val goal = intent.getStringExtra("EXTRA_GOAL") ?: dataManager.goal.ifEmpty { "Anxiety Relief" }
        val mood = intent.getStringExtra("EXTRA_MOOD") ?: dataManager.mood.ifEmpty { "Neutral" }
        val level = intent.getStringExtra("EXTRA_LEVEL") ?: dataManager.selectedLevel.ifEmpty { "Beginner" }

        // Calculate and show correct duration based on experience level
        val durationInt = when (level.trim().lowercase()) {
            "beginner"     -> 5
            "intermediate" -> 10
            "advanced"     -> 15
            else           -> 10
        }
        binding.txtDuration.text = "$durationInt min"

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
                callPredictEmotion(dataManager, mood, level, durationInt)
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

    private fun callPredictEmotion(
        dataManager: DataManager,
        mood: String,
        level: String,
        durationInt: Int
    ) {
        val userId = dataManager.currentUserId
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

                dataManager.predictedEmotion = response.predictedEmotion
                dataManager.lastDuration = durationInt

                val intent = Intent(this@PersonalizedInsightsActivity, AiPlanActivity::class.java)
                intent.putExtras(this@PersonalizedInsightsActivity.intent)
                intent.putExtra("EXTRA_PREDICTED_EMOTION", response.predictedEmotion)
                intent.putExtra("EXTRA_CONFIDENCE", response.confidence)
                intent.putExtra("EXTRA_LEVEL", level)
                intent.putExtra("EXTRA_DURATION", "$durationInt min")
                intent.putExtra("EXTRA_DURATION_INT", durationInt)
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
        val message = when (mood) {
            "Anxious", "Stressed" -> "Helps reduce stress & improves calm"
            "Tired", "Low"        -> "Revitalizes your energy & focus"
            "Neutral", "Normal"   -> "Maintains stability & clarity"
            "Happy", "Great",
            "Excited"             -> "Amplifies your positive state"
            else                  -> "Perfectly balanced for your state"
        }
        binding.txtAdaptiveMessage.text = message

        val sessionName = when (goal) {
            "Anxiety Relief"       -> "Oceanic\nCalm"
            "Better Sleep"         -> "Midnight\nDrift"
            "Focus & Productivity" -> "Peak\nClarity"
            "Build Self-Esteem"    -> "Soul\nStrength"
            else                   -> "Zen\nHarmony"
        }
        binding.txtSessionName.text = sessionName
    }
}