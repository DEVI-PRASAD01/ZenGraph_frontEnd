package com.simats.zengraph

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.simats.zengraph.databinding.ActivityReflectionBinding

class ReflectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReflectionBinding
    private var selectedMood: String = ""
    private var lastSelected: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReflectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMoodSelection()

        binding.btnAnalyzeProgress.setOnClickListener {
            val intent = Intent(this, PostSessionAiActivity::class.java)
            intent.putExtra("EXTRA_POST_MOOD", selectedMood)
            intent.putExtra("EXTRA_DURATION", this@ReflectionActivity.intent.getIntExtra("EXTRA_DURATION", 15))
            startActivity(intent)
        }
    }

    private fun setupMoodSelection() {
        val moods = mapOf(
            binding.moodCalm to "Calm",
            binding.moodRelaxed to "Relaxed",
            binding.moodFocused to "Focused",
            binding.moodAnxious to "Still Anxious"
        )

        moods.forEach { (view, mood) ->
            view.setOnClickListener {
                // Reset previous selection
                lastSelected?.setBackgroundResource(R.drawable.bg_stat_card)

                // Highlight selected
                view.setBackgroundResource(R.drawable.bg_goal_card_selected)
                lastSelected = view
                selectedMood = mood

                binding.selectedMoodLabel.text = "You're feeling: $mood"
            }
        }
    }
}
