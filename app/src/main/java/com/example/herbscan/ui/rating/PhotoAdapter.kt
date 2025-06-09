package com.example.herbscan.ui.rating

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.herbscan.databinding.ItemPhotoRatingBinding

class PhotoAdapter(
    private val photos: MutableList<Uri>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(private val binding: ItemPhotoRatingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(uri: Uri, position: Int) {
            binding.ivPhoto.setImageURI(uri)
            binding.ivDelete.setOnClickListener {
                onDeleteClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoRatingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position], position)
    }

    override fun getItemCount(): Int = photos.size

    fun addPhoto(uri: Uri) {
        photos.add(uri)
        notifyItemInserted(photos.size - 1)
    }

    fun removePhoto(position: Int) {
        if (position >= 0 && position < photos.size) {
            photos.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, photos.size)
        }
    }
}