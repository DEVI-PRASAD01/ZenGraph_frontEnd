package com.simats.zengraph

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.simats.zengraph.databinding.ActivityMeditationSessionBinding
import java.util.Locale

class MeditationSessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMeditationSessionBinding
    private var mediaPlayer: MediaPlayer? = null
    private var countDownTimer: CountDownTimer? = null
    private var isPlaying = false
    private var startTimeMillis: Long = 0L
    
    private val handler = Handler(Looper.getMainLooper())
    private val updateProgressAction = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    val currentPos = it.currentPosition
                    binding.audioProgressBar.progress = currentPos
                    binding.tvCurrentTime.text = formatTime(currentPos)
                    handler.postDelayed(this, 1000)
                }
            }
        }
    }

    private var remainingTimeMillis: Long = 10 * 60 * 1000 // Default 10 minutes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeditationSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupIntentData()
        setupMediaPlayer()
        setupListeners()
        updateTimerText(remainingTimeMillis)
    }

    private fun setupIntentData() {
        val sessionName = intent.getStringExtra("SESSION_NAME") ?: "Cosmic Calm Journey"
        val durationMinutes = intent.getIntExtra("DURATION_MINUTES", 10)
        
        binding.tvSessionTitle.text = sessionName
        binding.tvSessionDuration.text = "$durationMinutes Minute Meditation"
        
        remainingTimeMillis = (durationMinutes * 60 * 1000).toLong()
        binding.tvTotalTime.text = formatTime(remainingTimeMillis.toInt())
        binding.tvRemainingTime.text = formatTime(remainingTimeMillis.toInt())
    }

    private fun setupMediaPlayer() {
        try {
            // Note: Assumes res/raw/meditation_audio exists. 
            // If it doesn't, this will throw an exception or return null.
            val resId = resources.getIdentifier("meditation_audio", "raw", packageName)
            if (resId != 0) {
                mediaPlayer = MediaPlayer.create(this, resId)
                mediaPlayer?.setOnCompletionListener {
                    // Start next or loop if needed, but for now we follow the timer
                }
                binding.audioProgressBar.max = mediaPlayer?.duration ?: 0
            } else {
                // Fallback or warning if audio is missing
                binding.tvSessionTitle.append(" (No Audio Found)")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupListeners() {
        binding.btnPlayPause.setOnClickListener {
            if (isPlaying) {
                pauseMeditation()
            } else {
                playMeditation()
            }
        }

        binding.audioProgressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                    binding.tvCurrentTime.text = formatTime(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                handler.removeCallbacks(updateProgressAction)
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (isPlaying) {
                    handler.post(updateProgressAction)
                }
            }
        })

//        binding.btnClose.setOnClickListener {
//            finish()
//        }
    }

    private fun playMeditation() {
        if (startTimeMillis == 0L) {
            startTimeMillis = System.currentTimeMillis()
        }
        mediaPlayer?.start()
        isPlaying = true
        binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_pause)
        
        startTimer(remainingTimeMillis)
        handler.post(updateProgressAction)
    }

    private fun pauseMeditation() {
        mediaPlayer?.pause()
        isPlaying = false
        binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
        
        countDownTimer?.cancel()
        handler.removeCallbacks(updateProgressAction)
    }

    private fun startTimer(millisInFuture: Long) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(millisInFuture, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTimeMillis = millisUntilFinished
                updateTimerText(millisUntilFinished)
            }

            override fun onFinish() {
                remainingTimeMillis = 0
                updateTimerText(0)
                handleSessionComplete()
            }
        }.start()
    }

    private fun updateTimerText(millis: Long) {
        binding.tvRemainingTime.text = "Remaining: ${formatTime(millis.toInt())}"
    }

    private fun handleSessionComplete() {
        mediaPlayer?.stop()
        isPlaying = false
        binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
        handler.removeCallbacks(updateProgressAction)
        
        // Navigate to completion
        val intent = Intent(this, SessionCompleteActivity::class.java).apply {
            putExtra("COMPLETED_MINUTES", (remainingTimeMillis / 60000).toInt()) // Actually planned - remaining?
            // Fix: remainingTimeMillis is the remaining time on the timer.
            // Total planned duration was durationMinutes * 60 * 1000.
            val durationMinutes = intent.getIntExtra("DURATION_MINUTES", 10)
            val plannedMillis = durationMinutes * 60 * 1000L
            val actualCompletedMillis = plannedMillis - remainingTimeMillis
            
            putExtra("COMPLETED_MINUTES", (actualCompletedMillis / 60000).toInt())
            putExtra("EXTRA_PLANNED_MINUTES", durationMinutes)
            putExtra("EARLY_EXIT", false)
            putExtra("EXTRA_START_TIME", startTimeMillis)
            putExtra("EXTRA_END_TIME", System.currentTimeMillis())
            putExtra("SESSION_NAME", intent.getStringExtra("SESSION_NAME"))
        }
        startActivity(intent)
        finish()
    }

    private fun formatTime(millis: Int): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    override fun onStop() {
        super.onStop()
        if (isPlaying) {
            pauseMeditation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        countDownTimer?.cancel()
        handler.removeCallbacks(updateProgressAction)
    }
}
