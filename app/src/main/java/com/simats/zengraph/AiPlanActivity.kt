package com.simats.zengraph

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.simats.zengraph.databinding.ActivityAiPlanBinding
import com.simats.zengraph.network.GeneratePlanRequest
import com.simats.zengraph.network.RetrofitClient
import kotlinx.coroutines.launch

class AiPlanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAiPlanBinding
    private var isProcessing = false
    private var planGenerated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAiPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataManager = DataManager(this)

        val goal = intent.getStringExtra("EXTRA_GOAL") ?: dataManager.goal.ifEmpty { "Anxiety Relief" }
        val mood = intent.getStringExtra("EXTRA_MOOD") ?: dataManager.mood.ifEmpty { "Neutral" }
        val level = intent.getStringExtra("EXTRA_LEVEL") ?: dataManager.selectedLevel.ifEmpty { "Beginner" }

        // Update Aims based on Goal
        updateAims(goal)

        // Disable Start button until plan is generated
        binding.btnStartPlan.isEnabled = false
        binding.btnStartPlan.alpha = 0.5f

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnNotifications.setOnClickListener {
            Toast.makeText(this, "Notifications coming soon!", Toast.LENGTH_SHORT).show()
        }

        binding.btnStartPlan.setOnClickListener {
            if (planGenerated) {
                val intent = Intent(this, SessionReadyActivity::class.java)
                intent.putExtras(this.intent)
                intent.putExtra("EXTRA_DURATION", binding.txtDuration.text.toString())
                startActivity(intent)
            }
        }

        binding.btnChangePlan.setOnClickListener {
            finish()
        }

        // Call API immediately on screen open
        generatePlan(dataManager, goal, mood, level)
    }

    private fun updateAims(goal: String) {
        when (goal) {
            "Anxiety Relief" -> {
                binding.txtAim1.text = "Reduce stress & overthinking"
                binding.txtAim2.text = "Feel calm and focused"
            }
            "Better Sleep" -> {
                binding.txtAim1.text = "Relax the body for deep sleep"
                binding.txtAim2.text = "Quiet the racing mind"
            }
            "Focus & Productivity" -> {
                binding.txtAim1.text = "Enhance mental clarity"
                binding.txtAim2.text = "Minimize distractions"
            }
            "Build Self-Esteem" -> {
                binding.txtAim1.text = "Cultivate self-compassion"
                binding.txtAim2.text = "Overcome negative self-talk"
            }
            else -> {
                binding.txtAim1.text = "Improve overall well-being"
                binding.txtAim2.text = "Find inner tranquility"
            }
        }
    }

    private fun generatePlan(dataManager: DataManager, goal: String, mood: String, level: String) {
        val userId = dataManager.userId
        if (userId == -1) {
            Toast.makeText(this, "User session expired. Please login again.", Toast.LENGTH_LONG).show()
            return
        }

        if (isProcessing) return
        isProcessing = true

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.generatePlan(
                    GeneratePlanRequest(userId, goal, mood, level)
                )

                // Save plan data globally
                dataManager.planId = response.planId

                // Populate UI from API response
                binding.planName.text = response.title
                
                val fixedDuration = intent.getStringExtra("EXTRA_DURATION")
                if (fixedDuration != null) {
                    binding.txtDuration.text = fixedDuration
                } else {
                    binding.txtDuration.text = "${response.duration} min"
                }

                // Enable Start button after success
                planGenerated = true
                binding.btnStartPlan.isEnabled = true
                binding.btnStartPlan.alpha = 1.0f

            } catch (e: Exception) {
                Toast.makeText(
                    this@AiPlanActivity,
                    e.message ?: "Failed to generate plan",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                isProcessing = false
            }
        }
    }
}
