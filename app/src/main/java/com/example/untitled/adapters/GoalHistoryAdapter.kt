package com.example.untitled.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.untitled.models.GoalProgress
import com.example.untitled.R

class GoalHistoryAdapter(private var list: List<GoalProgress>) :
    RecyclerView.Adapter<GoalHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val amount: TextView = view.findViewById(R.id.tv_amount)
        val date: TextView = view.findViewById(R.id.tv_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_goal_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.amount.text = "+₹${item.amount_added}"
        holder.date.text = item.date_added
    }

    override fun getItemCount() = list.size

    fun update(newList: List<GoalProgress>) {
        list = newList
        notifyDataSetChanged()
    }
}