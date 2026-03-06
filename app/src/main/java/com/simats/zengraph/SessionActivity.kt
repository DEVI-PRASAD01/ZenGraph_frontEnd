package com.simats.zengraph

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import com.simats.zengraph.databinding.ActivitySessionBinding

class SessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionBinding
    private var isPlaying = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val goal = intent.getStringExtra("EXTRA_GOAL") ?: "Zen"
        val mood = intent.getStringExtra("EXTRA_MOOD") ?: "Calm"

        binding.sessionTitle.text = "Personalized $goal"
        binding.sessionSubtitle.text = "Optimized for your $mood mood"

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.playPauseButton.setOnClickListener {
            togglePlayback()
        }

        startBreathingAnimations()
    }

    private fun togglePlayback() {
        isPlaying = !isPlaying
        if (isPlaying) {
            binding.playPauseIcon.setImageResource(android.R.drawable.ic_media_pause)
            startBreathingAnimations()
        } else {
            binding.playPauseIcon.setImageResource(android.R.drawable.ic_media_play)
            stopBreathingAnimations()
        }
    }

    private fun startBreathingAnimations() {
        val breatheOut = ScaleAnimation(
            1.0f, 1.2f, 1.0f, 1.2f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 4000
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }

        val breatheIn = ScaleAnimation(
            0.8f, 1.1f, 0.8f, 1.1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 4000
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }

        binding.breathRingOuter.startAnimation(breatheOut)
        binding.breathRingInner.startAnimation(breatheIn)
    }

    private fun stopBreathingAnimations() {
        binding.breathRingOuter.clearAnimation()
        binding.breathRingInner.clearAnimation()
    }
}
