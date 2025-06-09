package com.example.herbscan.ui.detail.tab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.herbscan.R
import com.example.herbscan.data.network.firebase.Rating
import com.example.herbscan.utils.Utils.getRelativeTimeDifference

class RatingAdapter(private var list: MutableList<Rating>) : RecyclerView.Adapter<RatingAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val ivProfilePic: ImageView = itemView.findViewById(R.id.iv_profile_pic)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvRatingNumber: TextView = itemView.findViewById(R.id.tv_rating_number)
        val tvChat: TextView = itemView.findViewById(R.id.tv_chat)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        val rvPhotos: RecyclerView = itemView.findViewById(R.id.rv_photos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rating, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rating = list[position]

        holder.apply {
            Glide.with(itemView.context)
                .load(rating.profilePic)
                .into(ivProfilePic)
            tvName.text = rating.name
            tvRatingNumber.text = rating.rating.toString()
            tvChat.text = rating.desc
            val relativeTime = getRelativeTimeDifference(rating.time)
            tvTime.text = relativeTime

            val photoUrls = mutableListOf<String>()

            if (rating.image1.isNotEmpty()) photoUrls.add(rating.image1)
            if (rating.image2.isNotEmpty()) photoUrls.add(rating.image2)
            if (rating.image3.isNotEmpty()) photoUrls.add(rating.image3)
            if (rating.image4.isNotEmpty()) photoUrls.add(rating.image4)
            if (rating.image5.isNotEmpty()) photoUrls.add(rating.image5)

            if (photoUrls.isNotEmpty()) {
                rvPhotos.visibility = View.VISIBLE
                rvPhotos.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                rvPhotos.adapter = ListRatingPhotoAdapter(photoUrls)
            } else {
                rvPhotos.visibility = View.GONE
            }
        }
    }
}