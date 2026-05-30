package com.example.lifeledger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lifeledger.R
import com.example.lifeledger.models.Task
import com.example.lifeledger.models.TaskSection

class SectionedTasksAdapter(

    private var sections: List<TaskSection>,

    private val onTaskChecked:
        (Task, Boolean) -> Unit

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {

        const val TYPE_HEADER = 0

        const val TYPE_TASK = 1
    }

    private val items =
        mutableListOf<Any>()

    init {

        buildItems()
    }

    private fun buildItems() {

        items.clear()

        sections.forEach { section ->

            items.add(section)

            if(section.expanded) {

                items.addAll(section.tasks)
            }
        }
    }

    inner class HeaderViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        val tvTitle:
                TextView =
            view.findViewById(R.id.tv_section_title)

        val ivArrow:
                ImageView =
            view.findViewById(R.id.iv_arrow)
    }

    inner class TaskViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        val tvName:
                TextView =
            view.findViewById(R.id.tv_task_name)

        val tvTime:
                TextView =
            view.findViewById(R.id.tv_time)

        val cbTask:
                CheckBox =
            view.findViewById(R.id.cb_task)
        val tvPriority:
                TextView =
            view.findViewById(R.id.tv_priority)
        val tvRepeat:
                TextView =
            view.findViewById(R.id.tv_repeat)
        val tvDescription:
                TextView =
            view.findViewById(R.id.tv_description)

        val layoutAttachment:
                LinearLayout =
            view.findViewById(R.id.layout_attachment)
    }

    override fun getItemViewType(position: Int): Int {

        return if(
            items[position] is TaskSection
        ) TYPE_HEADER
        else TYPE_TASK
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        return if(viewType == TYPE_HEADER) {

            val view =
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_task_section,
                        parent,
                        false
                    )

            HeaderViewHolder(view)

        } else {

            val view =
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_task_row,
                        parent,
                        false
                    )

            TaskViewHolder(view)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {

        when(holder) {

            is HeaderViewHolder -> {

                val section =
                    items[position]
                            as TaskSection

                holder.tvTitle.text =
                    section.title
                holder.ivArrow.visibility =
                    if(sections.size == 1)
                        View.GONE
                    else
                        View.VISIBLE

                holder.ivArrow.rotation =
                    if(section.expanded)
                        90f
                    else
                        0f

                holder.itemView.setOnClickListener {

                    section.expanded =
                        !section.expanded

                    buildItems()

                    notifyDataSetChanged()
                }

            }

            is TaskViewHolder -> {

                val task =
                    items[position]
                            as Task

                holder.tvName.text =
                    task.title

                holder.tvTime.text =
                    formatDateTime(
                        task.date,
                        task.time
                    )
                if(task.description.isNullOrBlank()) {

                    holder.tvDescription.visibility =
                        View.GONE

                } else {

                    holder.tvDescription.visibility =
                        View.VISIBLE

                    holder.tvDescription.text =
                        task.description
                }

                holder.cbTask.setOnCheckedChangeListener(null)

                holder.cbTask.isChecked =
                    task.completed == 1

                holder.cbTask.setOnCheckedChangeListener { _, checked ->

                    onTaskChecked(task, checked)
                }
                holder.tvPriority.text =
                    task.priority?.uppercase()
                when(task.priority) {

                    "high" -> {

                        holder.tvPriority.setBackgroundResource(
                            R.drawable.bg_priority_high
                        )
                    }

                    "medium" -> {

                        holder.tvPriority.setBackgroundResource(
                            R.drawable.bg_priority_medium
                        )
                    }

                    else -> {

                        holder.tvPriority.setBackgroundResource(
                            R.drawable.bg_priority_low
                        )
                    }
                }

                /* REPEAT BADGE */

                if(task.repeat_type != "none") {

                    holder.tvRepeat.visibility =
                        View.VISIBLE

                    holder.tvRepeat.text =
                        "Repeats ${
                            task.repeat_type?.replaceFirstChar {
                                it.uppercase()
                            }
                        }"

                } else {

                    holder.tvRepeat.visibility =
                        View.GONE
                }
                if(task.attachment_uri.isNullOrEmpty()) {

                    holder.layoutAttachment.visibility =
                        View.GONE

                } else {

                    holder.layoutAttachment.visibility =
                        View.VISIBLE
                }
                holder.layoutAttachment.setOnClickListener {

                    val intent =
                        android.content.Intent(
                            android.content.Intent.ACTION_VIEW
                        )

                    intent.setDataAndType(
                        android.net.Uri.parse(
                            task.attachment_uri
                        ),
                        "image/*"
                    )

                    holder.itemView.context.startActivity(intent)
                }

                if(task.completed == 1) {

                    holder.tvName.paint.isStrikeThruText = true

                    holder.tvName.alpha = 0.5f

                } else {

                    holder.tvName.paint.isStrikeThruText = false

                    holder.tvName.alpha = 1f
                }
            }
        }
    }
    fun getItem(
        position: Int
    ): Any {

        return items[position]
    }

    override fun getItemCount() =
        items.size

    fun updateSections(
        newSections: List<TaskSection>
    ) {

        sections = newSections

        buildItems()

        notifyDataSetChanged()
    }
    private fun formatDateTime(
        date: String?,
        time: String?
    ): String {

        return try {

            val input =
                java.text.SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    java.util.Locale.getDefault()
                )

            val output =
                java.text.SimpleDateFormat(
                    "EEE, MMM d • hh:mm a",
                    java.util.Locale.getDefault()
                )

            val parsed =
                input.parse("$date $time")

            output.format(parsed!!)

        } catch(e: Exception) {

            "$date $time"
        }
    }
}