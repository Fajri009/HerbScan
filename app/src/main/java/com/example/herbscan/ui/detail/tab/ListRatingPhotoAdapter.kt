package com.example.herbscan.ui.detail.tab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.herbscan.R

class ListRatingPhotoAdapter(private val photoUrls: List<String>) : RecyclerView.Adapter<ListRatingPhotoAdapter.PhotoViewHolder>() {
    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPhoto: ImageView = itemView.findViewById(R.id.iv_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_photo_rating, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoUrl = photoUrls[position]

        Glide.with(holder.itemView.context)
            .load(photoUrl)
            .transform(RoundedCorners(16))
            .into(holder.ivPhoto)
    }

    override fun getItemCount(): Int = photoUrls.size
}