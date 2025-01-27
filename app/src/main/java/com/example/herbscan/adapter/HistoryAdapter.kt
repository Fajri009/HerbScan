package com.example.herbscan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.herbscan.R
import com.example.herbscan.data.local.room.HistoryEntity

class HistoryAdapter(private var list: List<HistoryEntity>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    private var onItemClickCallBack: OnItemClickCallBack? = null

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallBack = onItemClickCallBack
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPlantName: TextView = itemView.findViewById(R.id.tv_plant_name)
        val tvPlantScientificName: TextView = itemView.findViewById(R.id.tv_plant_scientific_name)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            tvPlantName.text = list[position].plantName
            tvPlantScientificName.text = list[position].plantScientificName
            tvDate.text = list[position].timeStamp

            itemView.setOnClickListener {
                onItemClickCallBack!!.onItemClicked(list[holder.adapterPosition])
            }
        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(data: HistoryEntity)
    }
}