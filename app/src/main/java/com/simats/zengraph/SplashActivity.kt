package com.simats.zengraph

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.simats.zengraph.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- Phase 1: Logo floats down from top and fades in ---
        binding.logoView.animate()
            .alpha(1f)
            .translationY(0f)
            .scaleX(1.0f)
            .scaleY(1.0f)
            .setDuration(1200)
            .setInterpolator(DecelerateInterpolator(2f))
            .withEndAction {
                // Gentle breathing pulse on the logo
                startBreathingAnimation()
            }
            .start()

        // --- Phase 2: Letters appear one by one with bounce-up effect ---
        val letters = listOf(
            binding.letterZ, binding.letterE, binding.letterN,
            binding.letterG, binding.letterR, binding.letterA,
            binding.letterP, binding.letterH
        )

        letters.forEachIndexed { index, textView ->
            textView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setStartDelay(800L + (index * 120L)) // staggered start
                .setInterpolator(OvershootInterpolator(1.5f))
                .start()
        }

        // --- Phase 3: Tagline fades in after all letters ---
        binding.tagline.animate()
            .alpha(1f)
            .setDuration(800)
            .setStartDelay(800L + (letters.size * 120L) + 300L)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // --- Navigate after animation completes ---
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, AuthChoiceActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 3800)
    }

    private fun startBreathingAnimation() {
        binding.logoView.animate()
            .scaleX(1.08f)
            .scaleY(1.08f)
            .setDuration(1500)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                binding.logoView.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(1500)
                    .setInterpolator(DecelerateInterpolator())
                    .withEndAction {
                        startBreathingAnimation()
                    }
                    .start()
            }
            .start()
    }
}
