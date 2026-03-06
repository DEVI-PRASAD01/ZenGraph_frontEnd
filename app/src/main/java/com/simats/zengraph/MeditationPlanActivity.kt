package com.simats.zengraph

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simats.zengraph.databinding.ActivityMeditationPlanBinding

class MeditationPlanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMeditationPlanBinding
    private lateinit var dataManager: DataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeditationPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataManager = DataManager(this)
        
        val currentDay = dataManager.currentPlanDay
        binding.planProgress.progress = currentDay
        binding.txtProgress.text = "Day $currentDay of 7 completed"

        val days = (1..7).map { PlanDay(it, it <= currentDay, it == currentDay + 1) }

        binding.timelineRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.timelineRecyclerView.adapter = PlanAdapter(days)
    }

    data class PlanDay(val dayNumber: Int, val isCompleted: Boolean, val isNext: Boolean)

    class PlanAdapter(private val days: List<PlanDay>) : RecyclerView.Adapter<PlanAdapter.ViewHolder>() {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val txtDay: TextView = view.findViewById(android.R.id.text1)
            val txtStatus: TextView = view.findViewById(android.R.id.text2)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val day = days[position]
            holder.txtDay.text = "Day ${day.dayNumber}: ${getTheme(day.dayNumber)}"
            holder.txtDay.setTextColor(Color.WHITE)
            
            holder.txtStatus.text = when {
                day.isCompleted -> "✓ Completed"
                day.isNext -> "▶ Start Session"
                else -> "🔒 Locked"
            }
            holder.txtStatus.setTextColor(if (day.isNext) Color.CYAN else Color.GRAY)
            
            holder.itemView.alpha = if (day.isCompleted || day.isNext) 1.0f else 0.5f
        }

        private fun getTheme(day: Int) = when(day) {
            1 -> "Foundation of Breath"
            2 -> "Exploring Stillness"
            3 -> "Body Awareness"
            4 -> "Calming the Chaos"
            5 -> "Deep Focus"
            6 -> "Emotional Balance"
            else -> "Mastery of Zen"
        }

        override fun getItemCount() = days.size
    }
}
