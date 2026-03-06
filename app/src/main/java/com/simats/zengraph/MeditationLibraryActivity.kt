package com.simats.zengraph

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.simats.zengraph.adapter.LibrarySessionAdapter
import com.simats.zengraph.databinding.ActivityMeditationLibraryBinding
import com.simats.zengraph.network.LibrarySession
import com.simats.zengraph.network.RetrofitClient
import com.simats.zengraph.network.LibraryGeneratePlanRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MeditationLibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMeditationLibraryBinding
    private lateinit var adapter: LibrarySessionAdapter

    private var allSessions: List<LibrarySession> = emptyList()
    private var selectedCategory: String = "All"

    // Fallback sessions if API is down
    private val fallbackSessions = listOf(
        LibrarySession(1, "Breathe & Release", "Anxiety Relief", 5),
        LibrarySession(2, "Morning Focus", "Deep Focus", 10),
        LibrarySession(3, "Deep Relaxation", "Sleep", 20),
        LibrarySession(4, "Stress Reset", "Anxiety Relief", 8),
        LibrarySession(5, "Micro Calm", "Quick Calm", 3),
        LibrarySession(6, "Mindful Walk", "Deep Focus", 15)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeditationLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupChips()

        fetchSessions()

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = LibrarySessionAdapter(emptyList()) { session ->
            onSessionClicked(session)
        }
        binding.rvSessions.layoutManager = GridLayoutManager(this, 2)
        binding.rvSessions.adapter = adapter
    }

    private fun setupChips() {
        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) {
                binding.chipAll.isChecked = true
                return@setOnCheckedStateChangeListener
            }
            val chip = group.findViewById<Chip>(checkedIds.first())
            selectedCategory = chip?.text?.toString() ?: "All"
            applyFilters()
        }
    }



    private fun applyFilters() {
        val filtered = if (selectedCategory == "All") {
            allSessions
        } else {
            allSessions.filter { it.category == selectedCategory }
        }

        adapter.updateList(filtered)
        binding.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun fetchSessions() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sessions = RetrofitClient.apiService.getLibrarySessions()
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    allSessions = sessions.ifEmpty { fallbackSessions }
                    applyFilters()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    // Use fallback sessions
                    allSessions = fallbackSessions
                    applyFilters()
                    Toast.makeText(this@MeditationLibraryActivity, "Using offline library", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onSessionClicked(session: LibrarySession) {
        val dataManager = DataManager(this)
        val userId = dataManager.userId.takeIf { it != -1 } ?: 1

        Toast.makeText(this, "Preparing ${session.title}...", Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = LibraryGeneratePlanRequest(
                    userId = userId,
                    title = session.title,
                    duration = session.duration,
                    category = session.category
                )
                val response = RetrofitClient.apiService.libraryGeneratePlan(request)
                dataManager.planId = response.planId

                runOnUiThread {
                    navigateToFeeling(session.category, response.title, response.duration)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    // Fallback with local data
                    navigateToFeeling(session.category, session.title, session.duration)
                }
            }
        }
    }

    private fun navigateToFeeling(category: String, title: String, duration: Int) {
        val dataManager = DataManager(this)
        val intent = Intent(this, FeelingSelectionActivity::class.java)
        intent.putExtra("EXTRA_GOAL", category)
        intent.putExtra("EXTRA_USER_ID", dataManager.userId)
        intent.putExtra("EXTRA_SOURCE", "LIBRARY")
        intent.putExtra("EXTRA_LIB_TITLE", title)
        intent.putExtra("EXTRA_LIB_DURATION", duration)
        intent.putExtra("EXTRA_DURATION", "$duration min")
        startActivity(intent)
    }
}
