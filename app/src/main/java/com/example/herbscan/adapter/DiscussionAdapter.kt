package com.example.herbscan.adapter

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.herbscan.R
import com.example.herbscan.data.network.firebase.Discussion
import com.example.herbscan.utils.Utils.getRelativeTimeDifference

class DiscussionAdapter(
    var list: ArrayList<Discussion>,
    private val onReplyClick: (Discussion) -> Unit
) : RecyclerView.Adapter<DiscussionAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProfilePic: ImageView = itemView.findViewById(R.id.iv_profile_pic)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvChat: TextView = itemView.findViewById(R.id.tv_chat)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        val tvReply: TextView = itemView.findViewById(R.id.tv_reply)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val discussion = list[position]

        holder.apply {
            Glide.with(itemView.context)
                .load(discussion.profilePic)
                .into(ivProfilePic)
            tvName.text = discussion.name
            tvChat.text = discussion.chat
            updateTime(tvTime, discussion.time)

            tvReply.setOnClickListener {
                onReplyClick(discussion)
            }
        }
    }

    // Fungsi untuk mengupdate waktu setiap detik
    private fun updateTime(tvTime: TextView, timestamp: String) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val relativeTime = getRelativeTimeDifference(timestamp)
                tvTime.text = relativeTime
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable)
    }
}