package com.simats.zengraph

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.simats.zengraph.databinding.ActivityExpressYourselfBinding
import java.io.IOException

class ExpressYourselfActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpressYourselfBinding
    private var mediaRecorder: MediaRecorder? = null
    private var audioPath: String? = null
    private var isRecording = false
    
    private var seconds = 0
    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            seconds++
            val mins = seconds / 60
            val secs = seconds % 60
            binding.timerText.text = String.format("%d : %02d", mins, secs)
            handler.postDelayed(this, 1000)
        }
    }

    private var goal: String? = null
    private var mood: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpressYourselfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goal = intent.getStringExtra("EXTRA_GOAL")
        mood = intent.getStringExtra("EXTRA_MOOD")

        audioPath = "${externalCacheDir?.absolutePath}/recording.3gp"

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.micContainer.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                if (checkPermissions()) {
                    startRecording()
                } else {
                    requestPermissions()
                }
            }
        }

        binding.analyzeButton.setOnClickListener {
            if (isRecording) stopRecording()
            val intent = android.content.Intent(this, AiAnalysisActivity::class.java)
            intent.putExtra("EXTRA_GOAL", goal)
            intent.putExtra("EXTRA_MOOD", mood)
            startActivity(intent)
        }


        binding.typeInstead.setOnClickListener {
            if (isRecording) stopRecording()
            // Redirect back to or show a text input
            onBackPressed()
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 200)
    }

    private fun startRecording() {
        try {
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(audioPath)
                prepare()
                start()
            }
            isRecording = true
            startTimer()
            startMicAnimation()
            binding.analyzeButton.alpha = 1.0f
            binding.analyzeButton.isEnabled = true
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Recording failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            stopTimer()
            stopMicAnimation()
            Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startTimer() {
        seconds = 0
        handler.post(timerRunnable)
    }

    private fun stopTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    private fun startMicAnimation() {
        val anim = ScaleAnimation(
            1.0f, 1.2f, 1.0f, 1.2f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        anim.duration = 1000
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = Animation.INFINITE
        binding.micContainer.startAnimation(anim)
    }

    private fun stopMicAnimation() {
        binding.micContainer.clearAnimation()
    }

    override fun onStop() {
        super.onStop()
        if (isRecording) {
            stopRecording()
        }
    }
}
