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
import android.graphics.Color
import android.content.res.ColorStateList
import android.text.SpannableString
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.View
import com.simats.zengraph.network.AdaptiveDurationResponse
import retrofit2.Response

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

        fetchAdaptiveDuration(dataManager.currentUserId)
    }

    private fun fetchAdaptiveDuration(userId: Int) {
        val dataManager = DataManager(this)
        
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getAdaptiveDuration(userId)
                if (response.isSuccessful && response.body()?.status == "success") {
                    val adaptiveResponse = response.body()!!
                    val adaptive = adaptiveResponse.adaptive_duration ?: dataManager.lastDuration
                    
                    binding.layoutAdaptiveDuration.visibility = View.VISIBLE

                    val labelText = "Adjusted to $adaptive min for you"
                    val spannable = SpannableString(labelText)
                    val mins = "$adaptive min"
                    val start = labelText.indexOf(mins)
                    if (start >= 0) {
                        spannable.setSpan(
                            ForegroundColorSpan(Color.parseColor("#185FA5")),
                            start, start + mins.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    binding.tvAdaptiveLabel.text = spannable
                    binding.tvAdaptiveSubtitle.text = "AI noticed you complete ~$adaptive min sessions"
                    binding.chipAdaptive.text = "$adaptive min"

                    dataManager.lastDuration = adaptive
                    highlightChip(binding.chipAdaptive)

                    binding.chip5min.setOnClickListener {
                        dataManager.lastDuration = 5
                        highlightChip(binding.chip5min)
                    }
                    binding.chipAdaptive.setOnClickListener {
                        dataManager.lastDuration = adaptive
                        highlightChip(binding.chipAdaptive)
                    }
                    binding.chip15min.setOnClickListener {
                        dataManager.lastDuration = 15
                        highlightChip(binding.chip15min)
                    }
                } else {
                    binding.layoutAdaptiveDuration.visibility = View.GONE
                }
            } catch (e: Exception) {
                binding.layoutAdaptiveDuration.visibility = View.GONE
            }
        }
    }

    private fun highlightChip(selected: com.google.android.material.chip.Chip) {
        listOf(binding.chip5min, binding.chipAdaptive, binding.chip15min).forEach { chip ->
            chip.chipBackgroundColor = ColorStateList.valueOf(
                if (chip == selected) Color.parseColor("#E6F1FB")
                else Color.parseColor("#F1F5F9")
            )
            chip.chipStrokeColor = ColorStateList.valueOf(
                if (chip == selected) Color.parseColor("#185FA5")
                else Color.parseColor("#CBD5E1")
            )
            chip.chipStrokeWidth = if (chip == selected) 2f else 1f
            chip.setTextColor(
                if (chip == selected) Color.parseColor("#185FA5")
                else Color.parseColor("#64748B")
            )
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
        val userId = dataManager.currentUserId.takeIf { it != -1 } ?: 1
        val title = intent.getStringExtra("EXTRA_LIB_TITLE") ?: "Meditation"
        val duration = intent.getIntExtra("EXTRA_LIB_DURATION", 10)

        isProcessing = true
        binding.btnBeginSession.isEnabled = false
        binding.btnBeginSession.alpha = 0.7f

        lifecycleScope.launch {
            try {
                val request = StartLibrarySessionRequest(
                    userId          = userId,
                    goal            = title,
                    moodBefore      = dataManager.mood.ifEmpty { "Neutral" },
                    experienceLevel = dataManager.selectedLevel.ifEmpty { "Beginner" },
                    sessionName     = title,
                    duration        = duration,
                    techniques      = "Library Session",
                    matchScore      = 100
                )
                val response = RetrofitClient.apiService.startLibrarySession(request)
                val sid = response.sessionId
                val finalDuration = duration

                SessionManager.currentSessionId = sid
                SessionManager.sessionDurationMinutes = finalDuration
                dataManager.sessionId = sid
                dataManager.lastDuration = finalDuration

                val navIntent = Intent(this@SessionReadyActivity, LiveSessionActivity::class.java)

                navIntent.putExtra("EXTRA_SESSION_ID", sid)
                navIntent.putExtra("EXTRA_DURATION_MINUTES", finalDuration)

// ✅ IMPORTANT ADDITIONS
                navIntent.putExtra("EXTRA_GOAL_CATEGORY", response.sessionName ?: title)
                navIntent.putExtra("EXTRA_MOOD", dataManager.mood.ifEmpty { "Neutral" })
                navIntent.putExtra("EXTRA_LEVEL", dataManager.selectedLevel.ifEmpty { "Beginner" })

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
        val userId = dataManager.currentUserId
        val planId = dataManager.planId

        if (userId == -1) {
            Toast.makeText(this, "User session expired. Please login again.", Toast.LENGTH_LONG).show()
            return
        }

        // planId check removed — session can start without a plan

        isProcessing = true
        binding.btnBeginSession.isEnabled = false
        binding.btnBeginSession.alpha = 0.7f

        lifecycleScope.launch {
            try {
                val goalStr = intent.getStringExtra("EXTRA_GOAL")?.ifEmpty { null }
                    ?: dataManager.goal.ifEmpty { "Anxiety Relief" }
                val moodStr = intent.getStringExtra("EXTRA_MOOD")?.ifEmpty { null }
                    ?: dataManager.mood.ifEmpty { "Neutral" }
                val levelStr = intent.getStringExtra("EXTRA_LEVEL")?.ifEmpty { null }
                    ?: dataManager.selectedLevel.ifEmpty { "Beginner" }
                val sessionName = (intent.getStringExtra("EXTRA_SESSION_NAME")?.ifEmpty { null }
                    ?: goalStr).ifEmpty { "Meditation Session" }

                // Duration based on experience level — this is the FINAL duration, never overridden
                val durationInt = when (levelStr.trim().lowercase()) {
                    "beginner"     -> 5
                    "intermediate" -> 10
                    "advanced"     -> 15
                    else           -> 10
                }
                dataManager.lastDuration = durationInt

                val request = SessionStartRequest(
                    userId          = userId,
                    goal            = goalStr,
                    moodBefore      = moodStr,
                    experienceLevel = levelStr,
                    sessionName     = sessionName,
                    duration        = durationInt,
                    techniques      = "Mindful Breathing",
                    matchScore      = 85
                )

                Toast.makeText(
                    this@SessionReadyActivity,
                    "Goal: $goalStr | Mood: $moodStr | Level: $levelStr | Duration: $durationInt",
                    Toast.LENGTH_LONG
                ).show()

                val response = RetrofitClient.apiService.startSession(request)
                val sid = response.sessionId

                SessionManager.currentSessionId = sid
                SessionManager.sessionDurationMinutes = durationInt
                dataManager.sessionId = sid

                val navIntent = Intent(this@SessionReadyActivity, LiveSessionActivity::class.java)

                navIntent.putExtras(this@SessionReadyActivity.intent)
                navIntent.putExtra("EXTRA_SESSION_ID", sid)
                navIntent.putExtra("EXTRA_DURATION_MINUTES", durationInt)

// ✅ FIXED + CONSISTENT KEYS
                navIntent.putExtra("EXTRA_GOAL_CATEGORY", goalStr)
                navIntent.putExtra("EXTRA_MOOD", moodStr)
                navIntent.putExtra("EXTRA_LEVEL", levelStr)

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
