package com.simats.zengraph

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.simats.zengraph.databinding.ActivitySessionReadyBinding
import com.simats.zengraph.network.RetrofitClient
import com.simats.zengraph.network.SessionStartRequest
import com.simats.zengraph.network.StartLibrarySessionRequest
import kotlinx.coroutines.launch

class SessionReadyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionReadyBinding
    private var isProcessing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessionReadyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataManager = DataManager(this)
        val goal = intent.getStringExtra("EXTRA_GOAL") ?: dataManager.goal.ifEmpty { "Anxiety Relief" }
        val duration = intent.getStringExtra("EXTRA_DURATION") ?: "10 min"

        // Update UI with session details
        binding.sessionTitle.text = goal
        binding.txtDurationBadge.text = duration
        binding.txtSessionDurationReminder.text = "$duration session"

        // Dynamic Checklist based on goal
        updateChecklist(goal)

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnBeginSession.setOnClickListener {
            if (!isProcessing) {
                startSession()
            }
        }

        binding.btnChangePlan.setOnClickListener {
            finish()
        }
    }

    private fun updateChecklist(goal: String) {
        when (goal) {
            "Anxiety Relief" -> {
                binding.txtStep1.text = "Grounding breathwork"
                binding.txtStep2.text = "Gentle body relaxation"
                binding.txtStep3.text = "Guided visualization"
            }
            "Better Sleep" -> {
                binding.txtStep1.text = "Progressive muscle relaxation"
                binding.txtStep2.text = "Nature sound ambient"
                binding.txtStep3.text = "Deep sleep induction"
            }
            "Focus & Productivity" -> {
                binding.txtStep1.text = "Single-point concentration"
                binding.txtStep2.text = "Mental clarity exercise"
                binding.txtStep3.text = "Strategic intent setting"
            }
            else -> {
                binding.txtStep1.text = "Calming breaths"
                binding.txtStep2.text = "Body awareness scan"
                binding.txtStep3.text = "Mindful presence"
            }
        }
    }

    private fun startSession() {
        val source = intent.getStringExtra("EXTRA_SOURCE") ?: "AI_PLAN"

        if (source == "LIBRARY") {
            startLibrarySession()
        } else {
            startAiPlanSession()
        }
    }

    /** Called when user comes from Meditation Library */
    private fun startLibrarySession() {
        val dataManager = DataManager(this)
        val userId = dataManager.userId.takeIf { it != -1 } ?: 1
        val title = intent.getStringExtra("EXTRA_LIB_TITLE") ?: "Meditation"
        val duration = intent.getIntExtra("EXTRA_LIB_DURATION", 10)

        isProcessing = true
        binding.btnBeginSession.isEnabled = false
        binding.btnBeginSession.alpha = 0.7f

        lifecycleScope.launch {
            try {
                val request = StartLibrarySessionRequest(
                    userId = userId,
                    title = title,
                    duration = duration
                )
                val response = RetrofitClient.apiService.startLibrarySession(request)
                val sid = response.sessionId
                val plannedDuration = response.plannedDuration.takeIf { it > 0 } ?: duration

                SessionManager.currentSessionId = sid
                SessionManager.sessionDurationMinutes = plannedDuration
                dataManager.sessionId = sid
                dataManager.lastDuration = plannedDuration

                val navIntent = Intent(this@SessionReadyActivity, LiveSessionActivity::class.java)
                navIntent.putExtra("EXTRA_SESSION_ID", sid)
                navIntent.putExtra("EXTRA_DURATION_MINUTES", plannedDuration)
                navIntent.putExtra("EXTRA_GOAL", response.sessionName ?: title)
                startActivity(navIntent)

            } catch (e: Exception) {
                Toast.makeText(
                    this@SessionReadyActivity,
                    e.message ?: "Failed to start session",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                isProcessing = false
                binding.btnBeginSession.isEnabled = true
                binding.btnBeginSession.alpha = 1.0f
            }
        }
    }

    /** Called when user comes from AI Plan flow */
    private fun startAiPlanSession() {
        val dataManager = DataManager(this)
        val userId = dataManager.userId
        val planId = dataManager.planId

        if (userId == -1) {
            Toast.makeText(this, "User session expired. Please login again.", Toast.LENGTH_LONG).show()
            return
        }

        if (planId == -1) {
            Toast.makeText(this, "No plan selected. Please go back.", Toast.LENGTH_LONG).show()
            return
        }

        isProcessing = true
        binding.btnBeginSession.isEnabled = false
        binding.btnBeginSession.alpha = 0.7f

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.startSession(
                    SessionStartRequest(userId, planId)
                )

                val sid = response.sessionId

                // Override server duration with intent-passed duration if available
                val intentDuration = intent.getStringExtra("EXTRA_DURATION")
                val durationMin = if (intentDuration != null) {
                    try {
                        intentDuration.split(" ")[0].toInt()
                    } catch (e: Exception) {
                        response.duration
                    }
                } else {
                    response.duration
                }

                SessionManager.currentSessionId = sid
                SessionManager.sessionDurationMinutes = durationMin
                dataManager.sessionId = sid

                val navIntent = Intent(this@SessionReadyActivity, LiveSessionActivity::class.java)
                navIntent.putExtras(this@SessionReadyActivity.intent)
                navIntent.putExtra("EXTRA_SESSION_ID", sid)
                navIntent.putExtra("EXTRA_DURATION_MINUTES", durationMin)
                startActivity(navIntent)

            } catch (e: Exception) {
                Toast.makeText(
                    this@SessionReadyActivity,
                    e.message ?: "Failed to start session",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                isProcessing = false
                binding.btnBeginSession.isEnabled = true
                binding.btnBeginSession.alpha = 1.0f
            }
        }
    }
}
