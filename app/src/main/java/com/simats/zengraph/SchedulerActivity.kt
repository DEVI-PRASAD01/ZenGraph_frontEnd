package com.simats.zengraph

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.simats.zengraph.databinding.ActivitySchedulerBinding

class SchedulerActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySchedulerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySchedulerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSaveSchedule.setOnClickListener {
            Toast.makeText(this, "Daily reminder set successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
