package com.simats.zengraph

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import com.simats.zengraph.databinding.ActivityAiAnalysisBinding

class AiAnalysisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAiAnalysisBinding
    private var goal: String? = null
    private var mood: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAiAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goal = intent.getStringExtra("EXTRA_GOAL")
        mood = intent.getStringExtra("EXTRA_MOOD")

        startAnimations()
        startDynamicStatus()

        // Simulate AI analysis for 6 seconds for a more "proper" feel
        Handler(Looper.getMainLooper()).postDelayed({
            proceedToExperienceSelection()
        }, 6000)
    }

    private fun startDynamicStatus() {
        val statuses = listOf(
            "Analyzing ${mood ?: "emotional"} patterns...",
            "Detecting vocal stress markers...",
            "Matching with ${goal ?: "meditation"} profiles...",
            "Optimizing neural resonance...",
            "Generating personalized session..."
        )

        var index = 0
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                if (index < statuses.size) {
                    binding.statusText.text = statuses[index]
                    index++
                    handler.postDelayed(this, 1200)
                }
            }
        }
        handler.post(runnable)
    }

    private fun startAnimations() {
        // Pulse brain icon
        val pulseBrain = ScaleAnimation(
            1.0f, 1.1f, 1.0f, 1.1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 1500
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
        binding.brainIcon.startAnimation(pulseBrain)

        // Rotate and pulse outer ring
        val rotateOuter = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 8000
            repeatCount = Animation.INFINITE
        }
        
        val pulseOuter = ScaleAnimation(
            1.0f, 1.2f, 1.0f, 1.2f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 2000
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
        binding.outerRing.startAnimation(rotateOuter)
        binding.outerRing.startAnimation(pulseOuter)

        // Pulse inner ring
        val pulseInner = ScaleAnimation(
            1.0f, 1.15f, 1.0f, 1.15f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 2500
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
        binding.innerRing.startAnimation(pulseInner)

        // Status star pulse
        val starPulse = ScaleAnimation(
            1.0f, 1.3f, 1.0f, 1.3f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 600
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
        binding.statusIcon.startAnimation(starPulse)
    }

    private fun proceedToExperienceSelection() {
        val intent = Intent(this, ExperienceSelectionActivity::class.java)
        intent.putExtras(this.intent)
        intent.putExtra("EXTRA_GOAL", goal)
        intent.putExtra("EXTRA_MOOD", mood)
        startActivity(intent)
        finish()
        // Add smooth horizontal transition
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
