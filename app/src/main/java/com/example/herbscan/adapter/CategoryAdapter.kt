package com.example.herbscan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.herbscan.R
import com.example.herbscan.data.network.firebase.Category

class CategoryAdapter(private var list: ArrayList<Category>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    private var onItemClickCallback : OnItemClickCallBack? = null
    private var selectedCategory: String = "Semua"

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategoryName: TextView = itemView.findViewById(R.id.tv_category_name)
        val layoutCategory: ConstraintLayout = itemView.findViewById(R.id.layout_category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_column_category, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = list[position]

        if (category.name == selectedCategory) {
            holder.layoutCategory.setBackgroundResource(R.drawable.background_selected_category)
        } else {
            holder.layoutCategory.setBackgroundResource(R.drawable.background_category)
        }

        val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
        if (position == 0) {
            val marginStartInPx = (30 * holder.itemView.context.resources.displayMetrics.density).toInt()
            params.marginStart = marginStartInPx
        } else {
            params.marginStart = 0
        }

        holder.apply {
            tvCategoryName.text = category.name

            itemView.setOnClickListener {
                selectedCategory = category.name
                notifyDataSetChanged()
                onItemClickCallback?.onItemClicked(category)
            }
        }
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickCallback = onItemClickCallBack
    }

    fun setSelectedCategory(category: String) {
        selectedCategory = category
        notifyDataSetChanged()
    }

    interface OnItemClickCallBack {
        fun onItemClicked(data: Category)
    }
}