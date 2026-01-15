package com.example.untitled.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.untitled.R
import com.example.untitled.data.local.entities.HabitEntity

class HabitsAdapter(
    private var habits: List<HabitEntity>,
    private val onHabitCheck: (HabitEntity) -> Unit
) : RecyclerView.Adapter<HabitsAdapter.HabitViewHolder>() {

    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_habit_title)
        val tvStreak: TextView = itemView.findViewById(R.id.tv_habit_frequency) // Reusing frequency textview for streak info
        val cbDone: CheckBox = itemView.findViewById(R.id.habit_checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit_row, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        holder.tvName.text = habit.title
        holder.tvStreak.text = "${habit.streak} day streak"
        
        // Temporarily detach listener to avoid recycling triggers
        holder.cbDone.setOnCheckedChangeListener(null)
        
        // This relies on 'isCompletedToday' field I added to Entity
        holder.cbDone.isChecked = habit.isCompletedToday
        
        holder.cbDone.setOnCheckedChangeListener { _, isChecked ->
             if (isChecked) onHabitCheck(habit)
        }
    }

    override fun getItemCount() = habits.size

    fun updateHabits(newHabits: List<HabitEntity>) {
        habits = newHabits
        notifyDataSetChanged()
    }
}
