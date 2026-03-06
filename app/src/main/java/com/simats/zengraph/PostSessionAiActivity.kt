package com.simats.zengraph

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.simats.zengraph.databinding.ActivityPostSessionAiBinding
import com.simats.zengraph.network.AIAnalysisRequest
import com.simats.zengraph.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostSessionAiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostSessionAiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostSessionAiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val postMood = intent.getStringExtra("EXTRA_POST_MOOD") ?: "Calm"
        val dataManager = DataManager(this)
        val preEmotion = dataManager.predictedEmotion.ifEmpty { "Neutral" }
        // Priority: intent extra > DataManager > default 15
        val duration = intent.getIntExtra("EXTRA_DURATION", 0).takeIf { it > 0 }
            ?: dataManager.lastDuration.takeIf { it > 0 }
            ?: 15

        android.util.Log.d("PostSessionAi", "API Request → pre_emotion=$preEmotion, post_mood=$postMood, duration=$duration")

        // Show loading and call real API
        binding.loadingView.visibility = View.VISIBLE
        binding.resultsView.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = AIAnalysisRequest(
                    preEmotion = preEmotion,
                    postMood = postMood,
                    duration = duration
                )
                val response = RetrofitClient.apiService.analyzeSession(request)

                runOnUiThread {
                    binding.loadingView.visibility = View.GONE
                    binding.resultsView.visibility = View.VISIBLE

                    binding.txtStressReduced.text = response.stressReduction
                    binding.txtCalmLevel.text = "${response.calmScore}"
                    binding.txtFocusImprovement.text = response.focusImprovement
                    binding.txtSuggestion.text = response.insight

                    dataManager.logSessionResult(
                        response.stressReduction,
                        "${response.calmScore}",
                        response.focusImprovement
                    )
                }
            } catch (e: Exception) {
                runOnUiThread {
                    binding.loadingView.visibility = View.GONE
                    binding.resultsView.visibility = View.VISIBLE
                    Toast.makeText(this@PostSessionAiActivity, "Analysis failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()

                    // Fallback values
                    binding.txtStressReduced.text = "N/A"
                    binding.txtCalmLevel.text = "N/A"
                    binding.txtFocusImprovement.text = "N/A"
                    binding.txtSuggestion.text = "Could not analyze session. Please try again later."
                }
            }
        }

        binding.btnViewProgress.setOnClickListener {
            val intent = Intent(this, AnalyticsDashboardActivity::class.java)
            startActivity(intent)
        }

        binding.btnGoHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}
