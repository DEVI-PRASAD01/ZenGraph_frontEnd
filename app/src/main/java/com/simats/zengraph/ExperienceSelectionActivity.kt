package com.simats.zengraph

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.simats.zengraph.databinding.ActivityExperienceSelectionBinding
import com.simats.zengraph.utils.AnimationUtils

class ExperienceSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExperienceSelectionBinding
    private var selectedLevelName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExperienceSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAnimations()
        setupListeners()
        setupCardSelection()
    }

    private fun setupAnimations() {
        AnimationUtils.apply3DEntrance(binding.title)
        AnimationUtils.apply3DEntrance(binding.subtitle, 100)
        AnimationUtils.apply3DEntrance(binding.cardBeginner, 200)
        AnimationUtils.apply3DEntrance(binding.cardIntermediate, 300)
        AnimationUtils.apply3DEntrance(binding.cardAdvanced, 400)
        AnimationUtils.apply3DEntrance(binding.continueButton, 500)
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            AnimationUtils.applyScalePop(it)
            onBackPressed()
        }

        binding.continueButton.setOnClickListener {
            if (selectedLevelName != null) {
                AnimationUtils.applyScalePop(it)
                // Always navigate to FeelingSelectionActivity for "full process"
                val intent = Intent(this, FeelingSelectionActivity::class.java)
                intent.putExtras(this.intent)
                intent.putExtra("EXTRA_LEVEL", selectedLevelName)
                startActivity(intent)
                overridePendingTransition(R.anim.anim_3d_enter, R.anim.anim_3d_exit)
                finish()
            }
        }
    }

    private fun setupCardSelection() {
        val cards = listOf(
            binding.cardBeginner to "Beginner",
            binding.cardIntermediate to "Intermediate",
            binding.cardAdvanced to "Advanced"
        )

        cards.forEach { (card, name) ->
            card.setOnClickListener {
                selectedLevelName = name
                selectLevel(card, cards.map { it.first })
            }
        }
    }

    private fun selectLevel(selectedCard: ConstraintLayout, allCards: List<ConstraintLayout>) {
        allCards.forEach { card ->
            if (card == selectedCard) {
                // Selected State: Dark background, light text, highlight
                card.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1A202C"))
                card.alpha = 1.0f
                
                // Update text colors for selected state
                (card.getChildAt(1) as? TextView)?.setTextColor(Color.WHITE) // Title
                (card.getChildAt(2) as? TextView)?.setTextColor(Color.parseColor("#CBD5E0")) // Description
                
                // Live Bounce Animation
                card.animate()
                    .scaleX(0.98f)
                    .scaleY(0.98f)
                    .setDuration(100)
                    .withEndAction {
                        card.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(200)
                            .setInterpolator(android.view.animation.OvershootInterpolator())
                            .start()
                    }
                    .start()
            } else {
                // Unselected State: White background, dark text
                card.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                card.alpha = 0.8f
                
                (card.getChildAt(1) as? TextView)?.setTextColor(Color.parseColor("#1A202C")) // Title
                (card.getChildAt(2) as? TextView)?.setTextColor(Color.parseColor("#4A5568")) // Description
                
                card.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
            }
        }

        // Enable continue button
        binding.continueButton.isEnabled = true
        binding.continueButton.alpha = 1.0f
    }
}
