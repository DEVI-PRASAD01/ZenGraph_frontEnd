package com.simats.zengraph

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.simats.zengraph.network.FriendStreakItem
import com.simats.zengraph.network.NudgeRequest
import com.simats.zengraph.network.RetrofitClient
import kotlinx.coroutines.launch

class FriendsActivity : AppCompatActivity() {

    private lateinit var friendsContainer: LinearLayout
    private val prefs by lazy { DataManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        friendsContainer = findViewById(R.id.friendsContainer)

        // Back button
        findViewById<ImageView>(R.id.btnBack)?.setOnClickListener { finish() }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAddFriend).setOnClickListener {
            showAddFriendDialog()
        }

        loadFriends()
    }

    private fun showAddFriendDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_friend, null)
        val etSearch   = dialogView.findViewById<android.widget.EditText>(R.id.etSearchFriend)
        val btnSearch  = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSearchFriend)
        val rvResults  = dialogView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvSearchResults)
        val tvNoResults = dialogView.findViewById<android.widget.TextView>(R.id.tvNoResults)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        rvResults.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this)

        btnSearch.setOnClickListener {
            val query = etSearch.text.toString().trim()
            if (query.isEmpty()) {
                android.widget.Toast.makeText(this,
                    "Type a name to search", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            searchUsers(query, rvResults, tvNoResults, dialog)
        }

        // Also search when user presses Enter on keyboard
        etSearch.setOnEditorActionListener { _, _, _ ->
            btnSearch.performClick()
            true
        }

        dialog.show()

        // Make dialog width match parent
        dialog.window?.setLayout(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun searchUsers(
        query: String,
        rvResults: androidx.recyclerview.widget.RecyclerView,
        tvNoResults: android.widget.TextView,
        dialog: androidx.appcompat.app.AlertDialog
    ) {
        val myUserId = DataManager(this).getUserId()

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.findUser(
                    name          = query,
                    currentUserId = myUserId
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == "success") {
                        val users = body.users
                        if (users.isEmpty()) {
                            tvNoResults.visibility = android.view.View.VISIBLE
                            rvResults.visibility   = android.view.View.GONE
                        } else {
                            tvNoResults.visibility = android.view.View.GONE
                            rvResults.visibility   = android.view.View.VISIBLE

                            rvResults.adapter = UserSearchAdapter(
                                users = users,
                                myUserId = myUserId,
                                onAddClick = { foundUser ->
                                    addFriend(foundUser.user_id, foundUser.name, dialog)
                                }
                            )
                        }
                    } else {
                        tvNoResults.visibility = android.view.View.VISIBLE
                        rvResults.visibility   = android.view.View.GONE
                    }
                } else {
                    tvNoResults.visibility = android.view.View.VISIBLE
                    rvResults.visibility   = android.view.View.GONE
                }
            } catch (e: Exception) {
                android.widget.Toast.makeText(
                    this@FriendsActivity,
                    "Search failed: ${e.localizedMessage}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun addFriend(
        friendId: Int,
        friendName: String,
        dialog: androidx.appcompat.app.AlertDialog
    ) {
        val myUserId = DataManager(this).getUserId()

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.addFriend(
                    com.simats.zengraph.network.AddFriendRequest(
                        user_id   = myUserId,
                        friend_id = friendId
                    )
                )
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.status == "success" || response.code() == 200) {
                        android.widget.Toast.makeText(
                            this@FriendsActivity,
                            "Added $friendName as a friend!",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                        loadFriends() 
                    } else {
                        android.widget.Toast.makeText(
                            this@FriendsActivity,
                            "Could not add friend: ${result?.message ?: "Unknown error"}",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    android.widget.Toast.makeText(
                        this@FriendsActivity,
                        "Server error adding friend",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                android.widget.Toast.makeText(
                    this@FriendsActivity,
                    "Error: ${e.localizedMessage}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadFriends() {
        val userId = prefs.getUserId()
        if (userId == -1) return

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getFriendStreaks(userId)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == "success") {
                        val friends = body.friends
                        friendsContainer.removeAllViews()
                        if (friends.isEmpty()) {
                            // Optionally show an empty state view here
                        } else {
                            friends.forEach { friend ->
                                createFriendCard(friend)
                            }
                            loadUnreadBadges()
                        }
                    } else {
                        Toast.makeText(this@FriendsActivity, "Error loading friends", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@FriendsActivity, "Error loading friends", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@FriendsActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createFriendCard(friend: FriendStreakItem) {
        val card = LayoutInflater.from(this).inflate(R.layout.item_friend_streak, friendsContainer, false)

        val tvFriendName = card.findViewById<TextView>(R.id.tvFriendName)
        val ivFlame    = card.findViewById<ImageView>(R.id.ivFlame)
        val layoutFlame = card.findViewById<FrameLayout>(R.id.layoutFlame)
        val tvStreak   = card.findViewById<TextView>(R.id.tvStreakCount)
        val tvStatus   = card.findViewById<TextView>(R.id.tvMeditatedStatus)
        val btnNudge   = card.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnNudge)
        val tvInitials = card.findViewById<TextView>(R.id.tvInitials)
        val circleView = card.findViewById<View>(R.id.viewInitialsCircle)

        // Avatar circle color by color_index
        val avatarColor = when (friend.color_index) {
            0    -> "#1D9E75"  // teal — self
            1    -> "#534AB7"  // purple
            2    -> "#D85A30"  // coral
            3    -> "#185FA5"  // blue
            4    -> "#D4537E"  // pink
            else -> "#888780"  // gray
        }
        val circleDraw = GradientDrawable()
        circleDraw.shape = GradientDrawable.OVAL
        circleDraw.setColor(Color.parseColor(avatarColor))
        circleView.background = circleDraw
        tvInitials.text = friend.initials

        tvFriendName.text = if (friend.is_self) "You" else friend.name
        tvStreak.text = friend.streak.toString()

        if (friend.meditated_today) {
            // Active — filled orange flame
            tvStatus.text = "Meditated today"
            tvStatus.setTextColor(Color.parseColor("#1D9E75"))
            ivFlame.setColorFilter(Color.parseColor("#F57C00"))
            tvStreak.setTextColor(Color.parseColor(avatarColor))
            val flameBg = GradientDrawable()
            flameBg.shape = GradientDrawable.OVAL
            flameBg.setColor(Color.parseColor("#FFF3E0"))
            layoutFlame.background = flameBg
            btnNudge.visibility = View.GONE
        } else {
            // Missed — gray flame
            tvStatus.text = "Missed today"
            tvStatus.setTextColor(Color.parseColor("#D85A30"))
            ivFlame.setColorFilter(Color.parseColor("#CCCCCC"))
            tvStreak.setTextColor(Color.parseColor("#CCCCCC"))
            val flameBgGray = GradientDrawable()
            flameBgGray.shape = GradientDrawable.OVAL
            flameBgGray.setColor(Color.parseColor("#F5F5F5"))
            layoutFlame.background = flameBgGray
            if (!friend.is_self) {
                btnNudge.visibility = View.VISIBLE
                btnNudge.text = "Nudge ${friend.name} to meditate today"
                btnNudge.setOnClickListener {
                    nudgeFriend(friend.user_id, friend.name)
                }
            } else {
                btnNudge.visibility = View.GONE
            }
        }

        card.tag = friend.user_id

        friendsContainer.addView(card)
    }

    private fun nudgeFriend(friendId: Int, name: String) {
        val userId = prefs.getUserId()
        if (userId == -1) return

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.nudgeFriend(NudgeRequest(userId, friendId))
                if (response.isSuccessful) {
                    Toast.makeText(this@FriendsActivity, "Nudged $name!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadUnreadBadges() {
        val myUserId = DataManager(this).getUserId()
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getUnreadCount(myUserId)
                if (response.isSuccessful && response.body()?.status == "success") {
                    val unreadMap = response.body()!!.unread_map
                    updateBadgesOnCards(unreadMap)
                }
            } catch (e: Exception) {
                // Badges are optional — silent fail
            }
        }
    }

    private fun updateBadgesOnCards(unreadMap: Map<String, Int>) {
        // Loop through friendsContainer children and add badges
        for (i in 0 until friendsContainer.childCount) {
            val card = friendsContainer.getChildAt(i)
            val tag  = card.tag
            if (tag is Int) {
                val count = unreadMap[tag.toString()] ?: 0
                val badge = card.findViewWithTag<android.widget.TextView>("badge_$tag")
                badge?.visibility = if (count > 0) View.VISIBLE else View.GONE
                badge?.text       = count.toString()
            }
        }
    }
}
