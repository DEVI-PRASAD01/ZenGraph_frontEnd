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

        // Calculate duration from experience level
        val durationInt = when (level.trim().lowercase()) {
            "beginner"     -> 5
            "intermediate" -> 10
            "advanced"     -> 15
            else           -> 10
        }
        val durationStr = "$durationInt min"

        // Show correct duration immediately
        binding.txtDuration.text = durationStr

        updateAims(goal)

        binding.btnStartPlan.isEnabled = false
        binding.btnStartPlan.alpha = 0.5f

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        binding.btnStartPlan.setOnClickListener {
            if (planGenerated) {
                val intent = Intent(this, SessionReadyActivity::class.java)
                intent.putExtras(this.intent)
                intent.putExtra("EXTRA_DURATION", durationStr)
                intent.putExtra("EXTRA_SESSION_NAME", binding.planName.text.toString())
                intent.putExtra("EXTRA_LEVEL", level)
                intent.putExtra("EXTRA_DURATION_INT", durationInt)
                startActivity(intent)
            }
        }

        binding.btnChangePlan.setOnClickListener {
            finish()
        }

        generatePlan(dataManager, goal, mood, level, durationStr)
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

    private fun generatePlan(
        dataManager: DataManager,
        goal: String,
        mood: String,
        level: String,
        durationStr: String
    ) {
        val userId = dataManager.currentUserId
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

                dataManager.planId = response.planId

                binding.planName.text = response.title

                // Always use experience-level duration, never backend response.duration
                binding.txtDuration.text = durationStr

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