package com.simats.zengraph

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simats.zengraph.databinding.ActivityChatBinding
import com.simats.zengraph.network.ChatMessage
import com.simats.zengraph.network.MarkReadRequest
import com.simats.zengraph.network.RetrofitClient
import com.simats.zengraph.network.SendMessageRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private var friendId   = -1
    private var friendName = ""
    private var myUserId   = -1
    private var friendStreak = 0
    private var friendColorIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        friendId         = intent.getIntExtra("FRIEND_ID", -1)
        friendName       = intent.getStringExtra("FRIEND_NAME") ?: "Friend"
        friendStreak     = intent.getIntExtra("FRIEND_STREAK", 0)
        friendColorIndex = intent.getIntExtra("FRIEND_COLOR_INDEX", 1)
        myUserId         = DataManager(this).getUserId()

        if (friendId == -1 || myUserId == -1) {
            finish()
            return
        }

        setupHeader()
        setupClickListeners()
        loadMessages()
    }

    private fun setupHeader() {
        binding.tvFriendName.text   = friendName
        binding.tvFriendStreak.text = "$friendStreak day streak"
        binding.tvFriendInitials.text = friendName.take(2).uppercase()

        val avatarColor = when (friendColorIndex) {
            1    -> "#534AB7"
            2    -> "#D85A30"
            3    -> "#185FA5"
            4    -> "#D4537E"
            else -> "#1D9E75"
        }
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        drawable.setColor(Color.parseColor(avatarColor))
        binding.viewFriendCircle.background = drawable
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnSend.setOnClickListener { sendMessage() }

        // Quick action buttons
        binding.btnQuick1.setOnClickListener {
            binding.etMessage.setText("Let's meditate now! Join me")
        }
        binding.btnQuick2.setOnClickListener {
            binding.etMessage.setText("Your streak is amazing! Keep going!")
        }
        binding.btnQuick3.setOnClickListener {
            binding.etMessage.setText("Want to join me for a session?")
        }
        binding.btnQuick4.setOnClickListener {
            binding.etMessage.setText("You missed today, don't give up! Meditate now")
        }

        binding.btnInviteSession.setOnClickListener {
            val sessionName = DataManager(this).sessionName.ifEmpty { "Meditation Session" }
            binding.etMessage.setText("Join me for $sessionName — let's meditate together!")
            sendMessage()
        }
    }

    private fun loadMessages() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getMessages(myUserId, friendId)
                if (response.isSuccessful && response.body()?.status == "success") {
                    val messages = response.body()!!.messages
                    displayMessages(messages)
                }
            } catch (e: Exception) {
                Toast.makeText(this@ChatActivity,
                    "Could not load messages", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayMessages(messages: List<ChatMessage>) {
        val friendInitials = friendName.take(2).uppercase()
        val avatarColor = when (friendColorIndex) {
            1    -> "#534AB7"
            2    -> "#D85A30"
            3    -> "#185FA5"
            4    -> "#D4537E"
            else -> "#1D9E75"
        }

        binding.rvMessages.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.rvMessages.adapter = MessagesAdapter(
            messages        = messages,
            friendInitials  = friendInitials,
            friendAvatarColor = avatarColor
        )

        // Scroll to bottom
        if (messages.isNotEmpty()) {
            binding.rvMessages.scrollToPosition(messages.size - 1)
        }
    }

    private fun sendMessage() {
        val text = binding.etMessage.text.toString().trim()
        if (text.isEmpty()) return

        binding.etMessage.setText("")

        lifecycleScope.launch {
            try {
                RetrofitClient.apiService.sendMessage(
                    SendMessageRequest(
                        sender_id   = myUserId,
                        receiver_id = friendId,
                        message     = text
                    )
                )
                // Reload messages to show new message
                loadMessages()
            } catch (e: Exception) {
                Toast.makeText(this@ChatActivity,
                    "Could not send message", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

class MessagesAdapter(
    private val messages: List<ChatMessage>,
    private val friendInitials: String,
    private val friendAvatarColor: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_SENT     = 1
        const val VIEW_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int) =
        if (messages[position].is_mine) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_sent, parent, false)
            SentViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_received, parent, false)
            ReceivedViewHolder(view)
        }
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messages[position]
        if (holder is SentViewHolder) {
            holder.tvText.text = msg.message
            holder.tvTime.text = msg.time
        } else if (holder is ReceivedViewHolder) {
            holder.tvText.text     = msg.message
            holder.tvTime.text     = msg.time
            holder.tvInitials.text = friendInitials
            val drawable = GradientDrawable()
            drawable.shape = GradientDrawable.OVAL
            drawable.setColor(Color.parseColor(friendAvatarColor))
            holder.viewCircle.background = drawable
        }
    }

    class SentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvText: TextView = view.findViewById(R.id.tvMessageText)
        val tvTime: TextView = view.findViewById(R.id.tvMessageTime)
    }

    class ReceivedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvText:     TextView = view.findViewById(R.id.tvMessageText)
        val tvTime:     TextView = view.findViewById(R.id.tvMessageTime)
        val tvInitials: TextView = view.findViewById(R.id.tvSenderInitials)
        val viewCircle: View     = view.findViewById(R.id.viewSenderCircle)
    }
}
