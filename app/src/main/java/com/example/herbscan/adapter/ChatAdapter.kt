package com.example.herbscan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.herbscan.R
import com.example.herbscan.data.network.firebase.Chat
import com.example.herbscan.utils.Utils.getRelativeTimeDifference

class ChatAdapter(private var list: MutableList<Chat>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProfilePic: ImageView = itemView.findViewById(R.id.iv_profile_pic)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvChat: TextView = itemView.findViewById(R.id.tv_chat)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = list[position]

        holder.apply {
            Glide.with(itemView.context)
                .load(chat.profilePic)
                .into(ivProfilePic)
            tvName.text = chat.name
            tvChat.text = chat.chat
            val relativeTime = getRelativeTimeDifference(chat.time)
            tvTime.text = relativeTime
        }
    }
}