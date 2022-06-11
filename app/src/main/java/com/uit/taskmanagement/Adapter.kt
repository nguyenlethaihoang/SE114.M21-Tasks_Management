package com.uit.taskmanagement

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_view.view.*

class Adapter(var data:ArrayList<CardInfo>) : RecyclerView.Adapter<Adapter.viewHolder>() {
    
    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.tvTitleView
        var priority: TextView = itemView.tvPriorityView
        var layout=itemView.mylayout

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val itemView= LayoutInflater.from(parent.context).
        inflate(R.layout.activity_view,parent,false)
        return viewHolder(itemView)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        when (data[position].done?.lowercase()) {
                        "yes" -> holder.layout.setBackgroundColor(Color.parseColor("#dfe6e9"))
                        "no" -> holder.layout.setBackgroundColor(Color.parseColor("#ffffff"))
        }
        when (data[position].priority?.lowercase()) {
            "high" -> holder.priority.setTextColor(Color.parseColor("#e74c3c"))
            "medium" -> holder.priority.setTextColor(Color.parseColor("#f1c40f"))
            "low" -> holder.priority.setTextColor(Color.parseColor("#3498db"))
        }
        val currentItem = data[position]
        holder.title.text = currentItem.title
        holder.priority.text = currentItem.priority
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateCardActivity::class.java)
            intent.putExtra("idUser", currentItem.idUser)
            intent.putExtra("idTask", currentItem.idTask)
            intent.putExtra("dataDate", currentItem.date)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
