package com.simats.zengraph

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simats.zengraph.network.UserSearchItem

class UserSearchAdapter(
    private val users: List<UserSearchItem>,
    private val myUserId: Int,
    private val onAddClick: (UserSearchItem) -> Unit
) : RecyclerView.Adapter<UserSearchAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val viewCircle: View   = view.findViewById(R.id.viewInitialsCircle)
        val tvInitials: TextView = view.findViewById(R.id.tvInitials)
        val tvName:     TextView = view.findViewById(R.id.tvUserName)
        val btnAdd: com.google.android.material.button.MaterialButton =
            view.findViewById(R.id.btnAddUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_search, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]

        holder.tvName.text    = user.name
        holder.tvInitials.text = user.name.take(2).uppercase()

        // Color by user_id
        val color = when (user.user_id % 5) {
            0    -> "#1D9E75"
            1    -> "#534AB7"
            2    -> "#D85A30"
            3    -> "#185FA5"
            else -> "#888780"
        }
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        drawable.setColor(Color.parseColor(color))
        holder.viewCircle.background = drawable
        holder.tvInitials.setTextColor(Color.WHITE)

        holder.btnAdd.setOnClickListener {
            onAddClick(user)
            holder.btnAdd.text = "Added ✓"
            holder.btnAdd.isEnabled = false
        }
    }
}
