package com.example.lifeledger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeledger.R
import com.example.lifeledger.models.Habit
import android.widget.ImageView

class HabitsAdapter(
    private var habits: List<Habit>,
    private val isTodayMode: Boolean,
    private val onHabitClick: (Habit) -> Unit,
    private val onHabitCheck: (Habit) -> Unit
) : RecyclerView.Adapter<HabitsAdapter.HabitViewHolder>() {

    class HabitViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val tvName:
                TextView =
            itemView.findViewById(
                R.id.tv_habit_title
            )

        val tvFrequency:
                TextView =
            itemView.findViewById(
                R.id.tv_habit_frequency
            )

        val cbDone:
                CheckBox =
            itemView.findViewById(
                R.id.habit_checkbox
            )
        val ivIcon:
                ImageView =
            itemView.findViewById(
                R.id.iv_habit_icon
            )

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HabitViewHolder {

        val view =
            LayoutInflater.from(
                parent.context
            ).inflate(
                R.layout.item_habit_row,
                parent,
                false
            )

        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: HabitViewHolder,
        position: Int
    ) {


        val habit = habits[position]
        val iconRes = when(habit.icon) {

            "fitness" ->
                R.drawable.ic_habit_fitness

            "reading" ->
                R.drawable.ic_habit_reading

            "water" ->
                R.drawable.ic_habit_water

            "running" ->
                R.drawable.ic_habit_running

            "music" ->
                R.drawable.ic_habit_music

            "journal" ->
                R.drawable.ic_habit_journal

            "nature" ->
                R.drawable.ic_habit_nature

            "art" ->
                R.drawable.ic_habit_art

            else ->
                R.drawable.ic_habit_fitness
        }

        holder.ivIcon.setImageResource(
            iconRes
        )

        holder.tvName.text =
            habit.name

        holder.tvFrequency.text =
            "${habit.streak} day streak"


        holder.cbDone.visibility =

            if(isTodayMode)
                View.VISIBLE
            else
                View.GONE

        holder.cbDone.setOnCheckedChangeListener(null)

        holder.cbDone.isChecked =
            habit.completed_today == 1

        holder.cbDone.isEnabled =
            habit.completed_today != 1

        holder.itemView.alpha =

            if(habit.completed_today == 1)
                0.7f
            else
                1f
        holder.cbDone.setOnCheckedChangeListener {
                _,
                isChecked ->

            if(isChecked){

                onHabitCheck(habit)
            }
        }
        holder.itemView.setOnClickListener {

            onHabitClick(habit)
        }
    }

    override fun getItemCount() =
        habits.size

    fun updateHabits(
        newHabits: List<Habit>
    ) {

        habits = newHabits

        notifyDataSetChanged()
    }
}