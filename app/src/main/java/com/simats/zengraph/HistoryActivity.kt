package com.simats.zengraph

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simats.zengraph.databinding.ActivityHistoryBinding
import com.simats.zengraph.network.RetrofitClient
import com.simats.zengraph.utils.AnimationUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import com.simats.zengraph.network.SessionHistoryItem
import com.simats.zengraph.network.SessionHistoryResponse

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { onBackPressed() }

        AnimationUtils.apply3DEntrance(binding.header)
        AnimationUtils.apply3DEntrance(binding.rvHistory, 200)

        val userId = getSharedPreferences("ZenGraph", Context.MODE_PRIVATE)
            .getInt("user_id", -1)

        if (userId != -1) {
            loadHistoryFromApi(userId)
        } else {
            binding.tvEmptyHistory.visibility = View.VISIBLE
            binding.rvHistory.visibility = View.GONE
        }
    }

    private fun loadHistoryFromApi(userId: Int) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getSessionHistory(userId)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val sessions = body.sessions ?: emptyList()
                    if (sessions.isEmpty()) {
                        binding.tvEmptyHistory.visibility = View.VISIBLE
                        binding.rvHistory.visibility = View.GONE
                    } else {
                        binding.tvEmptyHistory.visibility = View.GONE
                        binding.rvHistory.visibility = View.VISIBLE
                        binding.rvHistory.layoutManager = LinearLayoutManager(this@HistoryActivity)
                        binding.rvHistory.adapter = ApiHistoryAdapter(sessions)
                    }
                } else {
                    binding.tvEmptyHistory.visibility = View.VISIBLE
                    binding.rvHistory.visibility = View.GONE
                }
            } catch (e: Exception) {
                Toast.makeText(this@HistoryActivity,
                    "Could not load history", Toast.LENGTH_SHORT).show()
                binding.tvEmptyHistory.visibility = View.VISIBLE
                binding.rvHistory.visibility = View.GONE
            }
        }
    }
}

// Models removed from here: moved to network/SessionHistoryResponse.kt

// ── Adapter ────────────────────────────────────────────────────
class ApiHistoryAdapter(
    private val sessions: List<SessionHistoryItem>
) : RecyclerView.Adapter<ApiHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSessionName: TextView = view.findViewById(R.id.tvMeditationName)
        val tvDate:        TextView = view.findViewById(R.id.tvSavedTime)
        val tvStartTime:   TextView = view.findViewById(R.id.tvStartTime)
        val tvEndTime:     TextView = view.findViewById(R.id.tvEndTime)
        val tvGoal:        TextView = view.findViewById(R.id.tvGoal)
        val tvMood:        TextView = view.findViewById(R.id.tvMood)
        val tvLevel:       TextView = view.findViewById(R.id.tvLevel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = sessions.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val session = sessions[position]

        holder.tvSessionName.text = session.session_name ?: "Meditation Session"

        // Format date from started_at
        val dateStr = formatDate(session.started_at)
        holder.tvDate.text = dateStr

        holder.tvStartTime.text = formatTime(session.started_at)
        holder.tvEndTime.text   = formatTime(session.completed_at ?: session.started_at)
        holder.tvGoal.text      = session.goal ?: "-"
        holder.tvMood.text      = session.mood_before ?: session.mood_after ?: "Neutral"
        holder.tvLevel.text     = "Explorer"
    }

    private fun parseApiDate(dateStr: String?): Date? {
        if (dateStr.isNullOrEmpty()) return null
        val cleanDate = if (dateStr.contains(".")) dateStr.substringBefore(".") else if (dateStr.endsWith("Z")) dateStr.dropLast(1) else dateStr
        val normalizedStr = cleanDate.replace("T", " ").replace("Z", "")
        
        val inputFmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        // inputFmt.timeZone = TimeZone.getTimeZone("UTC") // Removed UTC override to use local time
        return try {
            inputFmt.parse(normalizedStr)
        } catch (e: Exception) {
            null
        }
    }

    private fun formatDate(dateStr: String?): String {
        val date = parseApiDate(dateStr)
        if (date != null) {
            val outputFmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            outputFmt.timeZone = TimeZone.getDefault()
            return outputFmt.format(date)
        }
        return dateStr?.take(10) ?: "-"
    }

    private fun formatTime(dateStr: String?): String {
        val date = parseApiDate(dateStr)
        if (date != null) {
            val outputFmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
            outputFmt.timeZone = TimeZone.getDefault()
            return outputFmt.format(date)
        }
        return "-"
    }
}