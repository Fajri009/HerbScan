package com.example.herbscan.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.herbscan.R
import com.example.herbscan.data.network.firebase.Plant

class ColumnPlantAdapter(private var list: ArrayList<Plant>) : RecyclerView.Adapter<ColumnPlantAdapter.ViewHolder>() {
    private var onItemClickCallback: OnItemClickCallBack? = null

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallback = onItemClickCallBack
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPlant: ImageView = itemView.findViewById(R.id.iv_plant)
        val tvPlantName: TextView = itemView.findViewById(R.id.tv_plant_name)
        val tvScientificName: TextView = itemView.findViewById(R.id.tv_plant_scientific_name)
        val tvPopularity: TextView = itemView.findViewById(R.id.tv_availability)
        val layoutRecommend: LinearLayout = itemView.findViewById(R.id.layout_recommend)
        val tvRecommend: TextView = itemView.findViewById(R.id.tv_recommend)
        val tvAvailability: TextView = itemView.findViewById(R.id.tv_availability)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_column_plant, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            Glide.with(itemView.context)
                .load(list[position].picture)
                .into(ivPlant)
            tvPlantName.text = list[position].name
            tvScientificName.text = list[position].scientificName
            tvPopularity.text = list[position].availability

            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            if (position == 0) {
                val marginStartInPx = (30 * holder.itemView.context.resources.displayMetrics.density).toInt()
                params.marginStart = marginStartInPx
            } else {
                params.marginStart = 0
            }

            if (list[position].recommendation.isBlank()) {
                layoutRecommend.visibility = View.GONE
            } else {
                tvRecommend.text = itemView.context.getString(R.string.recommendation_for, list[position].recommendation)
            }

            if (list[position].availability == "Umum") {
                tvAvailability.setTextColor(itemView.context.getColor(R.color.success_900))
            } else {
                tvAvailability.setTextColor(itemView.context.getColor(R.color.error_700))
            }

            itemView.setOnClickListener {
                onItemClickCallback?.onItemClicked(list[holder.adapterPosition]) ?: Log.i(TAG, "onItemClickCallback is null")
            }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(data: Plant)
    }

    companion object {
        const val TAG = "ColumnPlantAdapter"
    }
}