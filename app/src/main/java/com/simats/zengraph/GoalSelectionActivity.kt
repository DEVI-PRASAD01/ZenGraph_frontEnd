package com.simats.zengraph

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.simats.zengraph.databinding.ActivityGoalSelectionBinding
import com.simats.zengraph.utils.AnimationUtils

class GoalSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalSelectionBinding
    private var selectedGoalName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGoalSelection()

        // Pass along userId if it's coming from Login/Onboarding
        val userId = intent.getIntExtra("EXTRA_USER_ID", -1)

        binding.continueButton.setOnClickListener {
            if (selectedGoalName != null) {
                AnimationUtils.applyScalePop(it)
                val intent = Intent(this, ExperienceSelectionActivity::class.java)
                intent.putExtra("EXTRA_GOAL", selectedGoalName)
                intent.putExtra("EXTRA_USER_ID", userId)
                // Pass duration if exists
                intent.putExtra("EXTRA_DURATION", this.intent.getStringExtra("EXTRA_DURATION"))
                startAnimatedActivity(intent)
                finish()
            }
        }
    }

    private fun startAnimatedActivity(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.anim_3d_enter, R.anim.anim_3d_exit)
    }

    private fun setupGoalSelection() {
        val goals = listOf(
            binding.goalStress,
            binding.goalFocus,
            binding.goalSleep,
            binding.goalHappy,
            binding.goalCalm,
            binding.goalMindfulness
        )

        goals.forEach { goalView ->
            goalView.setOnClickListener {
                selectGoal(it as LinearLayout, goals)
            }
        }
    }

    private fun selectGoal(selectedView: LinearLayout, allGoals: List<LinearLayout>) {
        allGoals.forEach { view ->
            if (view == selectedView) {
                view.alpha = 1.0f
                // Selected state: darker background or border? 
                // For now, let's just use alpha and scale to match the 'neat' look
                view.setBackgroundResource(R.drawable.bg_glass_card)
                view.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#1A202C"))
                
                // Change text colors for selected state
                (view.getChildAt(0) as? TextView)?.setTextColor(android.graphics.Color.WHITE)
                
                // Live Bounce Animation
                view.animate()
                    .scaleX(0.98f)
                    .scaleY(0.98f)
                    .setDuration(100)
                    .withEndAction {
                        view.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(200)
                            .setInterpolator(android.view.animation.OvershootInterpolator())
                            .start()
                    }
                    .start()
                
                selectedGoalName = (view.getChildAt(0) as? TextView)?.text?.toString()
            } else {
                view.alpha = 0.8f
                view.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE)
                (view.getChildAt(0) as? TextView)?.setTextColor(android.graphics.Color.parseColor("#1A202C"))
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
            }
        }
        
        binding.continueButton.alpha = 1.0f
        binding.continueButton.isEnabled = true
    }
}
