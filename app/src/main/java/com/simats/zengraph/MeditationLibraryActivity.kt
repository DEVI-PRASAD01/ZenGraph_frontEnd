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
import com.simats.zengraph.network.StartLibrarySessionRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MeditationLibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMeditationLibraryBinding
    private lateinit var adapter: LibrarySessionAdapter

    private var allSessions: List<LibrarySession> = emptyList()
    private var selectedCategory: String = "All"

    private val fallbackSessions = listOf(
        LibrarySession(1, "Breathe & Release",  "Breathing Meditation",   5,  "Beginner",     "Breath Awareness", "Short breathing exercise to calm the mind"),
        LibrarySession(2, "Morning Focus",       "Mindfulness Meditation", 10, "Intermediate", "Mind Observation", "Build focus and mental clarity"),
        LibrarySession(3, "Deep Relaxation",     "Body Scan Meditation",   15, "Advanced",     "Body Scan",        "Release tension from every part of the body"),
        LibrarySession(4, "Stress Reset",        "Breathing Meditation",   5,  "Beginner",     "Breath Awareness", "Quick reset for stressful moments"),
        LibrarySession(5, "Peaceful Sleep",      "Sleep Meditation",       15, "Intermediate", "Relaxation",       "Prepare your mind for deep sleep"),
        LibrarySession(6, "Mindful Awareness",   "Mindfulness Meditation", 10, "Beginner",     "Mind Observation", "Cultivate present moment awareness")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeditationLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
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
        binding.tvEmpty.visibility     = View.GONE

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
                    allSessions = fallbackSessions
                    applyFilters()
                    Toast.makeText(
                        this@MeditationLibraryActivity,
                        "Using offline library",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun onSessionClicked(session: LibrarySession) {
        val dataManager = DataManager(this)
        val userId = dataManager.currentUserId.takeIf { it != -1 } ?: 1

        Toast.makeText(this, "Starting ${session.title}...", Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = StartLibrarySessionRequest(
                    userId          = userId,
                    goal            = categoryToGoal(session.category),
                    moodBefore      = "Neutral",
                    experienceLevel = session.level,
                    sessionName     = session.title,
                    duration        = session.duration,
                    techniques      = session.technique.ifEmpty { "Mindful Breathing" },
                    matchScore      = 90
                )

                val response = RetrofitClient.apiService.startLibrarySession(request)
                val sessionId = response.sessionId

                dataManager.sessionId    = sessionId
                dataManager.goal         = categoryToGoal(session.category)
                dataManager.lastDuration = session.duration

                SessionManager.currentSessionId       = sessionId
                SessionManager.sessionDurationMinutes = session.duration

                runOnUiThread {
                    val intent = Intent(
                        this@MeditationLibraryActivity,
                        LiveSessionActivity::class.java
                    ).apply {
                        putExtra("EXTRA_SESSION_ID",       sessionId)
                        putExtra("EXTRA_DURATION_MINUTES", session.duration)
                        putExtra("EXTRA_GOAL",             session.title)
                        putExtra("EXTRA_SESSION_NAME",     session.title)
                        putExtra("EXTRA_LEVEL",            session.level)
                        putExtra("SOURCE",                 "LIBRARY")
                    }
                    startActivity(intent)
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@MeditationLibraryActivity,
                        "Failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun categoryToGoal(category: String): String {
        return when (category.lowercase().trim()) {
            "breathing meditation"       -> "reduce_stress"
            "mindfulness meditation"     -> "build_mindfulness"
            "body scan meditation"       -> "increase_calm"
            "gratitude meditation"       -> "feel_happier"
            "loving-kindness meditation" -> "feel_happier"
            "sleep meditation"           -> "sleep_better"
            "anxiety relief", "stress"   -> "reduce_stress"
            "deep focus", "focus"        -> "improve_focus"
            "sleep", "better sleep"      -> "sleep_better"
            "calm", "quick calm"         -> "increase_calm"
            else                         -> "increase_calm"
        }
    }
}