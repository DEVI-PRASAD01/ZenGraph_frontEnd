package com.simats.zengraph.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simats.zengraph.databinding.ItemLibrarySessionBinding
import com.simats.zengraph.network.LibrarySession

class LibrarySessionAdapter(
    private var sessions: List<LibrarySession>,
    private val onClick: (LibrarySession) -> Unit
) : RecyclerView.Adapter<LibrarySessionAdapter.ViewHolder>() {

    // Category → teal-family color mapping
    private val categoryColors = mapOf(
        "Anxiety Relief" to 0xFF319795.toInt(),
        "Deep Focus" to 0xFF805AD5.toInt(),
        "Quick Calm" to 0xFF4FD1C5.toInt(),
        "Sleep" to 0xFF6B46C1.toInt()
    )

    inner class ViewHolder(val binding: ItemLibrarySessionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(session: LibrarySession) {
            binding.tvTitle.text = session.title
            binding.tvCategory.text = session.category
            binding.tvDuration.text = "${session.duration} min"

            // Tint the category badge
            val color = categoryColors[session.category] ?: 0xFF319795.toInt()
            binding.tvCategory.background.setTint(color)

            binding.root.setOnClickListener { onClick(session) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLibrarySessionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sessions[position])
    }

    override fun getItemCount() = sessions.size

    fun updateList(newSessions: List<LibrarySession>) {
        sessions = newSessions
        notifyDataSetChanged()
    }
}
