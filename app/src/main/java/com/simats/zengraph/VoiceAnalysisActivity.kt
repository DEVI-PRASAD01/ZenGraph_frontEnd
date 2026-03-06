package com.simats.zengraph

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import com.simats.zengraph.databinding.ActivityVoiceAnalysisBinding

class VoiceAnalysisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVoiceAnalysisBinding
    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRecordVoice.setOnClickListener {
            if (!isRecording) startRecording()
            else stopRecording()
        }

        binding.btnAnalyzeVoice.setOnClickListener {
            val intent = Intent(this, ExperienceSelectionActivity::class.java)
            // Transfer existing extras (including goal, mood, and userId)
            intent.putExtras(this.intent)
            startActivity(intent)
        }
    }

    private fun startRecording() {
        isRecording = true
        binding.txtRecordingStatus.text = "Listening... 0:10"
        
        // Start pulse animations
        startPulse(binding.pulseCircle1, 0L)
        startPulse(binding.pulseCircle2, 500L)
        
        // Simulate end of recording after 5 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            if (isRecording) stopRecording()
        }, 5000)
    }

    private fun stopRecording() {
        isRecording = false
        binding.txtRecordingStatus.text = "Recording Captured"
        binding.pulseCircle1.clearAnimation()
        binding.pulseCircle2.clearAnimation()
        binding.pulseCircle1.visibility = View.INVISIBLE
        binding.pulseCircle2.visibility = View.INVISIBLE
        
        showResults()
    }

    private fun showResults() {
        binding.micContainer.visibility = View.GONE
        binding.txtRecordingStatus.visibility = View.GONE
        binding.voiceResultCard.visibility = View.VISIBLE
        binding.btnAnalyzeVoice.text = "Continue to Meditation"
        
        // Randomize result for demo
        val emotions = listOf("Calm", "Neutrally Stable", "Slight Tension", "High Focus")
        binding.txtDetectedEmotion.text = "${emotions.random()} detected"
    }

    private fun startPulse(view: View, delay: Long) {
        val pulse = ScaleAnimation(
            1f, 1.5f, 1f, 1.5f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 1000
            startOffset = delay
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
        val fade = AlphaAnimation(0.5f, 0f).apply {
            duration = 1000
            startOffset = delay
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
        view.visibility = View.VISIBLE
        view.startAnimation(pulse)
        view.startAnimation(fade)
    }
}
