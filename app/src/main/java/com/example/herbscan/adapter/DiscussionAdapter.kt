package com.example.herbscan.adapter

import android.os.Handler
import android.os.Looper
import android.util.Log
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

class DiscussionAdapter(var list: ArrayList<Discussion>) : RecyclerView.Adapter<DiscussionAdapter.ViewHolder>() {
    private var onItemClickCallBack: OnItemClickCallBack? = null

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProfilePic: ImageView = itemView.findViewById(R.id.iv_profile_pic)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title_discussion)
        val tvTime: TextView = itemView.findViewById(R.id.tv_last_discussion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_discussion, parent, false)
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
            tvTitle.text = discussion.title
            Log.i("tester", "onBindViewHolder: ${discussion.time}")
            updateTime(tvTime, discussion.time)

            itemView.setOnClickListener {
                onItemClickCallBack?.onItemClicked(list[holder.adapterPosition])
            }
        }
    }

    private fun updateTime(tvTime: TextView, timestamp: String) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val relativeTime = getRelativeTimeDifference(timestamp)
                tvTime.text = "Dibuat $relativeTime"
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable)
    }

    interface OnItemClickCallBack {
        fun onItemClicked(data: Discussion)
    }
}