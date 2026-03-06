package com.simats.zengraph

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.simats.zengraph.databinding.ActivityPrivacyDataBinding

class PrivacyDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivacyDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.switchAnalytics.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "Enabled" else "Disabled"
            Toast.makeText(this, "Usage Analytics $status", Toast.LENGTH_SHORT).show()
        }

        binding.switchNotif.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "Enabled" else "Disabled"
            Toast.makeText(this, "Push Notifications $status", Toast.LENGTH_SHORT).show()
        }

        binding.btnDownload.setOnClickListener {
            Toast.makeText(this, "Preparing your data for download...", Toast.LENGTH_LONG).show()
        }

        binding.btnClearCache.setOnClickListener {
            Toast.makeText(this, "App cache cleared", Toast.LENGTH_SHORT).show()
        }

        binding.btnDeleteAccount.setOnClickListener {
            Toast.makeText(this, "Security confirmation required to delete account", Toast.LENGTH_LONG).show()
        }
    }
}
