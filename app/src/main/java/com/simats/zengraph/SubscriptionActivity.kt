package com.simats.zengraph

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.simats.zengraph.databinding.ActivitySubscriptionBinding

class SubscriptionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubscriptionBinding
    private var selectedPlan = "yearly"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.planMonthly.setOnClickListener {
            selectedPlan = "monthly"
            updateUI()
        }

        binding.planYearly.setOnClickListener {
            selectedPlan = "yearly"
            updateUI()
        }

        binding.btnSubscribe.setOnClickListener {
            Toast.makeText(this, "Processing your $selectedPlan subscription...", Toast.LENGTH_LONG).show()
            
            // Mock successful subscription
            binding.btnSubscribe.postDelayed({
                Toast.makeText(this, "Welcome to ZenGraph Pro!", Toast.LENGTH_SHORT).show()
                finish()
            }, 2000)
        }
    }

    private fun updateUI() {
        if (selectedPlan == "monthly") {
            binding.planMonthly.backgroundTintList = ContextCompat.getColorStateList(this, R.color.glass_white)
            binding.planYearly.backgroundTintList = ContextCompat.getColorStateList(this, R.color.glass_white_less)
            binding.btnSubscribe.text = "Start Monthly Plan"
        } else {
            binding.planMonthly.backgroundTintList = ContextCompat.getColorStateList(this, R.color.glass_white_less)
            binding.planYearly.backgroundTintList = ContextCompat.getColorStateList(this, R.color.glass_white)
            binding.btnSubscribe.text = "Start Free Trial (Yearly)"
        }
    }
}
