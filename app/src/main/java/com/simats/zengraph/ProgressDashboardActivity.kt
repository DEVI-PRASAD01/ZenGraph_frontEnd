package com.simats.zengraph

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simats.zengraph.databinding.ActivityProgressDashboardBinding

class ProgressDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgressDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgressDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataManager = DataManager(this)
        binding.txtStreak.text = dataManager.streak.toString()
        binding.txtTotalMinutes.text = dataManager.totalMinutes.toString()

        binding.btnReturnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
