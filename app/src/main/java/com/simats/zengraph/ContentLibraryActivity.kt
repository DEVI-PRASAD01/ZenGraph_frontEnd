package com.simats.zengraph

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simats.zengraph.databinding.ActivityContentLibraryBinding

class ContentLibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContentLibraryBinding
    private val sessions = listOf(
        MeditationSession("Breathe & Release", "Anxiety Relief", "5 min", "Beginner"),
        MeditationSession("Morning Focus", "Deep Focus", "10 min", "Intermediate"),
        MeditationSession("Deep Relaxation", "Better Sleep", "20 min", "Advanced"),
        MeditationSession("Stress Reset", "Anxiety Relief", "8 min", "Intermediate"),
        MeditationSession("Micro-Calm", "Quick Calm", "3 min", "Beginner"),
        MeditationSession("Mindful Walk", "Deep Focus", "15 min", "Beginner")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.libraryRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.libraryRecyclerView.adapter = LibraryAdapter(sessions) { session ->
            val intent = Intent(this, SessionReadyActivity::class.java)
            intent.putExtra("EXTRA_SESSION_NAME", session.name)
            startActivity(intent)
        }
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    data class MeditationSession(val name: String, val category: String, val duration: String, val difficulty: String)

    class LibraryAdapter(private val list: List<MeditationSession>, private val onClick: (MeditationSession) -> Unit) :
        RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(android.R.id.text1)
            val sub: TextView = view.findViewById(android.R.id.text2)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]
            holder.name.text = item.name
            holder.name.setTextColor(android.graphics.Color.WHITE)
            holder.sub.text = "${item.category} • ${item.duration}"
            holder.sub.setTextColor(android.graphics.Color.GRAY)
            holder.itemView.setOnClickListener { onClick(item) }
        }

        override fun getItemCount() = list.size
    }
}
