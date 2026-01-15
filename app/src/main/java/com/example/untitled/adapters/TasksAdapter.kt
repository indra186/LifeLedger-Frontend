package com.example.untitled.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.untitled.R
import com.example.untitled.data.local.entities.TaskEntity

class TasksAdapter(
    private var tasks: List<TaskEntity>,
    private val onTaskChecked: (TaskEntity, Boolean) -> Unit
) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_task_name)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        val cbTask: CheckBox = itemView.findViewById(R.id.cb_task)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task_row, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.tvName.text = task.title
        holder.tvTime.text = "${task.date ?: ""} ${task.time ?: ""}".trim()
        holder.cbTask.setOnCheckedChangeListener(null) // Prevent recycling triggers
        holder.cbTask.isChecked = task.isCompleted
        
        holder.cbTask.setOnCheckedChangeListener { _, isChecked ->
             onTaskChecked(task, isChecked)
        }
    }

    override fun getItemCount() = tasks.size

    fun updateTasks(newTasks: List<TaskEntity>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}
