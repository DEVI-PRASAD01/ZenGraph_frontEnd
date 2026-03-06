package com.simats.zengraph

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simats.zengraph.databinding.ActivityAiCoachChatBinding
import com.simats.zengraph.network.CoachChatRequest
import com.simats.zengraph.network.RecommendedSession
import com.simats.zengraph.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class AiCoachChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAiCoachChatBinding
    private lateinit var dataManager: DataManager
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter

    // Speech-to-text launcher
    private val speechLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull() ?: ""
            if (spokenText.isNotBlank()) {
                binding.edtMessage.setText(spokenText)
                binding.edtMessage.setSelection(spokenText.length)
                // Auto-send the recognized text
                sendCurrentMessage()
            }
        }
    }

    // Permission launcher for microphone
    private val micPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startSpeechRecognition()
        else Toast.makeText(this, "Microphone permission required", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAiCoachChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataManager = DataManager(this)
        setupChat()
        loadHistory()

        binding.btnBack.setOnClickListener { finish() }

        // Send button
        binding.btnSend.setOnClickListener { sendCurrentMessage() }

        // Mic button — speech to text
        binding.btnMic.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED
            ) {
                startSpeechRecognition()
            } else {
                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }

        // Suggestion chips
        binding.suggestAnxious.setOnClickListener { quickSend("I feel anxious") }
        binding.suggestFocus.setOnClickListener { quickSend("I need help focusing") }
        binding.suggestSleep.setOnClickListener { quickSend("I can't sleep") }

        // Enter key sends message
        binding.edtMessage.setOnEditorActionListener { _, _, _ ->
            sendCurrentMessage()
            true
        }

        // Welcome message if no history
        if (messages.isEmpty()) {
            addMessage("Hi! I'm your AI wellness coach. How are you feeling today? 🧘", isUser = false)
        }
    }

    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to your coach...")
        }
        try {
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Speech recognition not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupChat() {
        adapter = ChatAdapter(messages) { session ->
            // Call API directly and open meditation timer
            startRecommendedSession(session)
        }
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.chatRecyclerView.adapter = adapter
    }

    private fun startRecommendedSession(session: RecommendedSession) {
        val userId = dataManager.userId.takeIf { it != -1 } ?: 1

        Toast.makeText(this, "Starting ${session.title}...", Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = com.simats.zengraph.network.StartLibrarySessionRequest(
                    userId = userId,
                    title = session.title,
                    duration = session.duration
                )
                val response = RetrofitClient.apiService.startLibrarySession(request)
                val sid = response.sessionId
                val plannedDuration = response.plannedDuration.takeIf { it > 0 } ?: session.duration

                SessionManager.currentSessionId = sid
                dataManager.sessionId = sid
                dataManager.lastDuration = plannedDuration

                runOnUiThread {
                    val intent = Intent(this@AiCoachChatActivity, LiveSessionActivity::class.java)
                    intent.putExtra("EXTRA_SESSION_ID", sid)
                    intent.putExtra("EXTRA_DURATION_MINUTES", plannedDuration)
                    intent.putExtra("EXTRA_GOAL", session.title)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    // Fallback: navigate with local duration
                    val intent = Intent(this@AiCoachChatActivity, LiveSessionActivity::class.java)
                    intent.putExtra("EXTRA_DURATION_MINUTES", session.duration)
                    intent.putExtra("EXTRA_GOAL", session.title)
                    startActivity(intent)
                }
            }
        }
    }

    private fun quickSend(text: String) {
        binding.edtMessage.setText(text)
        sendCurrentMessage()
    }

    private fun sendCurrentMessage() {
        val text = binding.edtMessage.text.toString().trim()
        if (text.isBlank()) return

        binding.edtMessage.text.clear()
        addMessage(text, isUser = true)
        callCoachApi(text)
    }

    private fun callCoachApi(userMessage: String) {
        val userId = dataManager.userId.takeIf { it != -1 } ?: 1

        // Show typing indicator
        addMessage("Thinking... 🤔", isUser = false, isTyping = true)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = CoachChatRequest(userId = userId, message = userMessage)
                val response = RetrofitClient.apiService.coachChat(request)

                runOnUiThread {
                    // Remove typing indicator
                    removeTypingIndicator()

                    // Add AI reply
                    addMessage(
                        text = response.reply,
                        isUser = false,
                        recommendedSession = response.recommendedSession
                    )
                }
            } catch (e: Exception) {
                runOnUiThread {
                    removeTypingIndicator()
                    addMessage(
                        "I'm having trouble connecting right now. Please try again in a moment.",
                        isUser = false
                    )
                }
            }
        }
    }

    private fun removeTypingIndicator() {
        val idx = messages.indexOfLast { it.isTyping }
        if (idx != -1) {
            messages.removeAt(idx)
            adapter.notifyItemRemoved(idx)
        }
    }

    private fun addMessage(
        text: String,
        isUser: Boolean,
        recommendedSession: RecommendedSession? = null,
        isTyping: Boolean = false
    ) {
        messages.add(ChatMessage(text, isUser, recommendedSession, isTyping))
        adapter.notifyItemInserted(messages.size - 1)
        binding.chatRecyclerView.scrollToPosition(messages.size - 1)
        if (!isTyping) saveHistory()
    }

    private fun loadHistory() {
        val historyJson = dataManager.chatHistory
        if (historyJson.isBlank() || historyJson == "[]") return
        try {
            val arr = JSONArray(historyJson)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                messages.add(ChatMessage(obj.getString("text"), obj.getBoolean("isUser")))
            }
            adapter.notifyDataSetChanged()
        } catch (_: Exception) {}
    }

    private fun saveHistory() {
        val arr = JSONArray()
        for (msg in messages.filter { !it.isTyping }) {
            val obj = JSONObject()
            obj.put("text", msg.text)
            obj.put("isUser", msg.isUser)
            arr.put(obj)
        }
        dataManager.chatHistory = arr.toString()
    }

    // === Data classes ===

    data class ChatMessage(
        val text: String,
        val isUser: Boolean,
        val recommendedSession: RecommendedSession? = null,
        val isTyping: Boolean = false
    )

    // === Adapter ===

    class ChatAdapter(
        private val messages: List<ChatMessage>,
        private val onSessionClick: (RecommendedSession) -> Unit
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        companion object {
            const val TYPE_USER = 1
            const val TYPE_AI = 0
        }

        inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        }

        inner class AiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvMessage: TextView = view.findViewById(R.id.tvMessage)
            val btnSession: TextView = view.findViewById(R.id.btnRecommendedSession)
        }

        override fun getItemViewType(position: Int) =
            if (messages[position].isUser) TYPE_USER else TYPE_AI

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == TYPE_USER) {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_user, parent, false)
                UserViewHolder(view)
            } else {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_ai, parent, false)
                AiViewHolder(view)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val msg = messages[position]
            when (holder) {
                is UserViewHolder -> {
                    holder.tvMessage.text = msg.text
                }
                is AiViewHolder -> {
                    holder.tvMessage.text = msg.text
                    val session = msg.recommendedSession
                    if (session != null) {
                        holder.btnSession.text = "▶ Start ${session.duration} min ${session.title}"
                        holder.btnSession.visibility = View.VISIBLE
                        holder.btnSession.setOnClickListener { onSessionClick(session) }
                    } else {
                        holder.btnSession.visibility = View.GONE
                    }
                }
            }
        }

        override fun getItemCount() = messages.size
    }
}
