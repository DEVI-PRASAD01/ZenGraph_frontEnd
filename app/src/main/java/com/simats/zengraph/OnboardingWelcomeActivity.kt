package com.simats.zengraph

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simats.zengraph.databinding.ActivityOnboardingWelcomeBinding
import com.simats.zengraph.utils.AnimationUtils

class OnboardingWelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAnimations()
        setupListeners()
        setupSwipeDetection()
    }

    private fun setupAnimations() {
        // Apply 3D Entrance to content elements
        AnimationUtils.apply3DEntrance(binding.speechBubbleContainer, 200)
        AnimationUtils.apply3DEntrance(binding.ivMascot, 400)
        AnimationUtils.apply3DEntrance(binding.contentGroup, 600)
        AnimationUtils.apply3DEntrance(binding.btnGetStarted, 800)

        // Start continuous "3D Animated Move" mascot animation
        // Floating + RotationY oscillation for a premium 3D feel
        AnimationUtils.startFloatingAnimation(binding.ivMascot, offset = 20f, duration = 3000L)
        
        // Infinite 3D-like horizontal rotation loop
        val rotateAnimator = android.animation.ObjectAnimator.ofFloat(binding.ivMascot, "rotationY", -15f, 15f).apply {
            duration = 4000L
            repeatMode = android.animation.ObjectAnimator.REVERSE
            repeatCount = android.animation.ObjectAnimator.INFINITE
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
        }
        rotateAnimator.start()

        // Also apply a subtle floating to the speech bubble to stay in sync
        AnimationUtils.startFloatingAnimation(binding.speechBubbleContainer, offset = 8f, duration = 3000L)
    }

    private fun setupListeners() {
        binding.btnGetStarted.setOnClickListener {
            navigateNext()
        }
    }

    private fun setupSwipeDetection() {
        val gestureDetector = android.view.GestureDetector(this, object : android.view.GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: android.view.MotionEvent?, e2: android.view.MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                val diffX = (e1?.x ?: 0f) - e2.x
                if (diffX > 100 && Math.abs(velocityX) > 100) {
                    // Swipe Left (implies "Next")
                    navigateNext()
                    return true
                }
                return false
            }
        })

        binding.root.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun navigateNext() {
        AnimationUtils.applyScalePop(binding.btnGetStarted)
        val intent = Intent(this, GoalSelectionActivity::class.java)
        // Pass userId if available, though it might be stored in SharedPreferences
        startActivity(intent)
        overridePendingTransition(R.anim.anim_3d_enter, R.anim.anim_3d_exit)
        finish()
    }
}
