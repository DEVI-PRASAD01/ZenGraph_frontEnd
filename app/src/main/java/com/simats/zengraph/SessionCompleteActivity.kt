package com.simats.zengraph

import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import com.simats.zengraph.databinding.ActivitySessionCompleteBinding

class SessionCompleteActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionCompleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessionCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Read extras using the specific keys requested by the user
        val completedMinutes = intent.getIntExtra("COMPLETED_MINUTES", 0)
        val plannedMinutes = intent.getIntExtra("EXTRA_PLANNED_MINUTES", 15)
        val isPartial = intent.getBooleanExtra("EARLY_EXIT", false)

        // Save last session duration for AI analysis
        val dataManager = DataManager(this)
        dataManager.lastDuration = completedMinutes

        // Set visual text using backend-provided duration
        binding.txtDurationDisplay.text = "$completedMinutes of $plannedMinutes minutes completed"
        
        // Calculate and set circular progress
        val progressPercent = if (plannedMinutes > 0) {
            ((completedMinutes.toFloat() / plannedMinutes.toFloat()) * 100).toInt()
        } else 100
        binding.completionProgress.progress = progressPercent

        if (isPartial) {
            binding.titleComplete.text = "Session Completed"
            binding.txtMotivational.text = "Progress is a journey. Well done!"
        } else {
            binding.titleComplete.text = "Session Completed"
            binding.txtMotivational.text = "Great job showing up today."
        }

        saveToHistory(
            intent.getLongExtra("EXTRA_START_TIME", System.currentTimeMillis()),
            intent.getLongExtra("EXTRA_END_TIME", System.currentTimeMillis()),
            intent.getStringExtra("SESSION_NAME") ?: "Meditation Session"
        )

        // Simple fade-in for motivational text
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 1000
            startOffset = 500
            fillAfter = true
        }
        binding.txtMotivational.startAnimation(fadeIn)

        // Navigate to Reflection
        binding.btnReflect.setOnClickListener {
            val intent = Intent(this, ReflectionActivity::class.java)
            intent.putExtra("EXTRA_DURATION", completedMinutes)
            startActivity(intent)
        }

        // Skip to Home
        binding.btnSkipHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun saveToHistory(startMillis: Long, endMillis: Long, sessionName: String) {
        val dataManager = DataManager(this)
        val sdf = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
        val dateSdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
        
        val currentTime = System.currentTimeMillis()
        
        val historyItem = HistoryItem(
            startTime = sdf.format(java.util.Date(startMillis)),
            endTime = sdf.format(java.util.Date(endMillis)),
            savedTime = dateSdf.format(java.util.Date(currentTime)),
            goal = dataManager.goal.ifEmpty { "Relaxation" },
            mood = dataManager.mood.ifEmpty { "Neutral" },
            level = dataManager.level,
            suggestedMeditation = sessionName
        )
        
        dataManager.saveHistoryItem(historyItem)
    }
}
