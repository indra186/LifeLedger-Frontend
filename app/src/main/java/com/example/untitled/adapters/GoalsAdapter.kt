package com.example.untitled.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.untitled.R
import com.example.untitled.models.Goal

class GoalsAdapter(
    private var goals: List<Goal>,
    private val onClick: (Goal) -> Unit
) : RecyclerView.Adapter<GoalsAdapter.GoalViewHolder>() {

    class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_goal_name)
        val tvAmount: TextView = itemView.findViewById(R.id.tv_goal_amount)
        val pbProgress: ProgressBar = itemView.findViewById(R.id.pb_goal_progress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_goal_card, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]

        holder.tvName.text = goal.title
        holder.tvAmount.text = "₹${goal.current_amount} / ₹${goal.target_amount}"

        val progress = if (goal.target_amount > 0) {
            ((goal.current_amount / goal.target_amount) * 100).toInt()
        } else 0

        holder.pbProgress.progress = progress

        holder.itemView.setOnClickListener {
            onClick(goal)
        }
    }

    override fun getItemCount(): Int = goals.size

    fun updateGoals(newGoals: List<Goal>) {
        goals = newGoals
        notifyDataSetChanged()
    }
}