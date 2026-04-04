package com.simats.zengraph

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simats.zengraph.databinding.ItemHistoryBinding

class HistoryAdapter(private val historyList: List<HistoryItem>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]
        holder.binding.apply {
            tvMeditationName.text = item.suggestedMeditation
            tvSavedTime.text = item.savedTime
            tvStartTime.text = item.startTime
            tvEndTime.text = item.endTime
            tvGoal.text = item.goal
            tvMood.text = item.mood
            tvLevel.text = item.level
        }
    }

    override fun getItemCount(): Int = historyList.size
}
