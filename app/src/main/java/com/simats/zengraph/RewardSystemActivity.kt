package com.simats.zengraph

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simats.zengraph.databinding.ActivityRewardSystemBinding

class RewardSystemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRewardSystemBinding
    private lateinit var dataManager: DataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardSystemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataManager = DataManager(this)

        binding.txtStreakDays.text = "${dataManager.streak} Days"
        binding.txtLevelName.text = dataManager.level
        
        val xpVal = dataManager.xp
        val nextLevelXp = 1000 // Simplified
        binding.txtXpVal.text = "$xpVal / $nextLevelXp XP"
        binding.xpProgressBar.progress = (xpVal * 100 / nextLevelXp)

        binding.btnBackHome.setOnClickListener {
            val intent = android.content.Intent(this, MainActivity::class.java)
            intent.flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
