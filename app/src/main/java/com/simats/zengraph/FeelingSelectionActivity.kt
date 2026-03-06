package com.simats.zengraph

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.simats.zengraph.databinding.ActivityFeelingSelectionBinding
import com.simats.zengraph.utils.AnimationUtils
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.simats.zengraph.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FeelingSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeelingSelectionBinding
    private var selectedMood: String = "Neutral"
    private var goal: String? = null
    private var lastSelectedView: android.view.View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeelingSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goal = intent.getStringExtra("EXTRA_GOAL")
        val userId = intent.getIntExtra("EXTRA_USER_ID", -1)

        setupDate()
        setupMoodSelection()

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.continueButton.setOnClickListener {
            if (userId != -1) {
                val moodScore = when (selectedMood) {
                    "Excited" -> 10.0f
                    "Happy" -> 8.0f
                    "Neutral" -> 5.0f
                    "Anxious" -> 4.0f
                    "Sad" -> 2.0f
                    "Angry" -> 1.0f
                    else -> 5.0f
                }
                
                val thought = binding.editTextThought.text.toString()
                
                kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                    try {
                        RetrofitClient.apiService.checkIn(
                            com.simats.zengraph.network.CheckInRequest(
                                userId, 
                                moodScore
                            )
                        )
                    } catch (e: Exception) {}
                }
            }

            val intent = Intent(this, PersonalizedInsightsActivity::class.java)
            intent.putExtras(this.intent)
            intent.putExtra("EXTRA_GOAL", goal)
            intent.putExtra("EXTRA_MOOD", selectedMood)
            intent.putExtra("EXTRA_USER_ID", userId)
            startAnimatedActivity(intent)
        }
    }

    private fun setupDate() {
        val sdf = java.text.SimpleDateFormat("d MMMM yyyy", java.util.Locale.getDefault())
        val date = sdf.format(java.util.Date())
        
        // Add ordinal suffix (st, nd, rd, th)
        val day = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH)
        val suffix = when {
            day in 11..13 -> "th"
            day % 10 == 1 -> "st"
            day % 10 == 2 -> "nd"
            day % 10 == 3 -> "rd"
            else -> "th"
        }
        
        val formattedDate = date.replaceFirst(day.toString(), "$day$suffix")
        binding.tvDate.text = formattedDate
    }

    private fun startAnimatedActivity(intent: Intent, finishCurrent: Boolean = false) {
        startActivity(intent)
        overridePendingTransition(R.anim.anim_3d_enter, R.anim.anim_3d_exit)
        if (finishCurrent) finish()
    }

    private fun setupMoodSelection() {
        val moods = listOf(
            binding.moodHappy to "Happy",
            binding.moodSad to "Sad",
            binding.moodAngry to "Angry",
            binding.moodAnxious to "Anxious",
            binding.moodNeutral to "Neutral",
            binding.moodExcited to "Excited"
        )

        // Default selection
        selectMood(binding.moodNeutral, "Neutral")

        moods.forEach { (view, mood) ->
            view.setOnClickListener {
                selectMood(view, mood)
            }
        }
    }

    private fun selectMood(selectedView: android.view.View, mood: String) {
        selectedMood = mood
        
        // Reset previous selection
        lastSelectedView?.let {
            it.setBackgroundResource(R.drawable.bg_mood_card)
            it.alpha = 0.6f
        }

        // Highlight selected
        selectedView.setBackgroundResource(R.drawable.bg_tab_selected) // Use teal border drawable
        selectedView.alpha = 1.0f
        lastSelectedView = selectedView

        // Animation
        selectedView.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                selectedView.animate()
                    .scaleX(1.05f)
                    .scaleY(1.05f)
                    .setDuration(200)
                    .setInterpolator(android.view.animation.OvershootInterpolator())
                    .start()
            }
            .start()
    }
}
