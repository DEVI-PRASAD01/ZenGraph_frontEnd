package com.simats.zengraph

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.simats.zengraph.databinding.ActivityLiveSessionBinding
import com.simats.zengraph.network.RetrofitClient
import com.simats.zengraph.network.SessionCompleteRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class LiveSessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLiveSessionBinding
    private var isPlaying = true
    private var timer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var totalSessionMillis: Long = 0
    private var isProcessing = false

    private val breathingHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val durationMinutes = intent.getIntExtra("EXTRA_DURATION_MINUTES", 15)
        totalSessionMillis = durationMinutes.toLong() * 60 * 1000
        timeLeftInMillis = totalSessionMillis

        binding.sessionTitle.text =
            intent.getStringExtra("EXTRA_GOAL") ?: "Breathing Meditation"

        binding.backButton.setOnClickListener {
            showPauseBottomSheet()
        }

        binding.btnPause.setOnClickListener {
            showPauseBottomSheet()
        }

        binding.btnVolume.setOnClickListener {
            Toast.makeText(this, "Volume Adjusted", Toast.LENGTH_SHORT).show()
        }

        binding.btnSleepMode.setOnClickListener {
            Toast.makeText(this, "Focus Mode Active", Toast.LENGTH_SHORT).show()
        }

        startTimer()
        startGlowAnimation()
    }

    private fun startTimer() {
        timer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerText()
            }

            override fun onFinish() {
                binding.timerText.text = "00:00"
                completeSession(isEarlyExit = false)
            }
        }.start()
        isPlaying = true
    }

    private fun updateTimerText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        binding.timerText.text =
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun showPauseBottomSheet() {
        timer?.cancel()
        isPlaying = false
        stopGlowAnimations()

        val bottomSheet = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_pause, null)

        view.findViewById<TextView>(R.id.btnResume).setOnClickListener {
            bottomSheet.dismiss()
            startTimer()
            startGlowAnimation()
        }

        view.findViewById<TextView>(R.id.btnEndSession).setOnClickListener {
            bottomSheet.dismiss()
            completeSession(isEarlyExit = true)
        }

        bottomSheet.setContentView(view)
        bottomSheet.setOnCancelListener {
            startTimer()
            startGlowAnimation()
        }
        bottomSheet.show()
    }

    private fun startGlowAnimation() {
        val pulse = ScaleAnimation(
            1.0f, 1.15f, 1.0f, 1.15f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 4000
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }

        val fade = AlphaAnimation(0.3f, 0.6f).apply {
            duration = 4000
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }

        binding.ringOuter.startAnimation(pulse)
        binding.timerGlow.startAnimation(fade)
    }

    private fun stopGlowAnimations() {
        binding.ringOuter.clearAnimation()
        binding.timerGlow.clearAnimation()
    }

    // 🔥 FINAL FIXED SESSION COMPLETION LOGIC
    private fun completeSession(isEarlyExit: Boolean = false) {
        if (isProcessing) return
        isProcessing = true

        timer?.cancel()
        stopGlowAnimations()

        binding.btnPause.isEnabled = false

        val dataManager = DataManager(this)
        
        // Priority for sessionId: Intent > SessionManager > DataManager fallback
        val sessionId = intent.getIntExtra("EXTRA_SESSION_ID", -1)
            .takeIf { it != -1 }
            ?: SessionManager.currentSessionId 
            ?: dataManager.sessionId

        // Calculate actual duration in minutes (round up to ensure 0 doesn't happen if they played for few seconds)
        val elapsedMillis = totalSessionMillis - timeLeftInMillis
        val elapsedMinutes = (elapsedMillis / (1000 * 60)).toInt().coerceAtLeast(1)
        val plannedMinutes = (totalSessionMillis / (1000 * 60)).toInt()

        if (sessionId == -1) {
            // Even if session ID is missing, navigate to completion screen to avoid getting stuck
            navigateToCompletion(-1, elapsedMinutes, plannedMinutes, isEarlyExit)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Call API to complete session — use backend duration_minutes
                val completeResponse = RetrofitClient.apiService.completeSession(SessionCompleteRequest(sessionId))
                val backendDuration = completeResponse.durationMinutes.takeIf { it > 0 } ?: elapsedMinutes

                SessionManager.clearSession()
                dataManager.clearSession()

                runOnUiThread {
                    navigateToCompletion(sessionId, backendDuration, plannedMinutes, isEarlyExit)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    // Still navigate even on failure — use local elapsed time as fallback
                    navigateToCompletion(sessionId, elapsedMinutes, plannedMinutes, isEarlyExit)
                }
            }
        }
    }

    private fun navigateToCompletion(sessionId: Int, elapsed: Int, planned: Int, isEarlyExit: Boolean) {
        val intent = Intent(this, SessionCompleteActivity::class.java).apply {
            putExtra("SESSION_ID", sessionId)
            putExtra("COMPLETED_MINUTES", elapsed)
            putExtra("EARLY_EXIT", isEarlyExit)
            putExtra("EXTRA_DURATION_MINUTES", elapsed)
            putExtra("EXTRA_PLANNED_MINUTES", planned)
            putExtra("IS_PARTIAL", isEarlyExit)
        }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}