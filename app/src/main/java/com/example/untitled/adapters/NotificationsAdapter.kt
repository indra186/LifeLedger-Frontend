package com.example.untitled.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.untitled.R
import com.example.untitled.databinding.ItemNotificationBinding
import com.example.untitled.models.Notification

class NotificationsAdapter(

    private var notifications:
    MutableList<Notification>,

    private val onClick:
        (Notification) -> Unit

) : RecyclerView.Adapter<
        NotificationsAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(

        val binding:
        ItemNotificationBinding

    ) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHolder {

        val binding =
            ItemNotificationBinding.inflate(

                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NotificationViewHolder,
        position: Int
    ) {

        val item =
            notifications[position]

        holder.binding.tvTitle.text =
            item.title

        holder.binding.tvMessage.text =
            item.message

        holder.binding.tvTime.text =
            getTimeAgo(
                item.created_at
            )
        holder.binding.tvMessage.setOnClickListener {

            val isExpanded =
                holder.binding.tvMessage.maxLines > 2

            if(isExpanded) {

                holder.binding.tvMessage.maxLines = 2
                holder.binding.tvMessage.ellipsize =
                    android.text.TextUtils.TruncateAt.END

            } else {

                holder.binding.tvMessage.maxLines = Int.MAX_VALUE
                holder.binding.tvMessage.ellipsize = null
            }
        }

        when(item.type) {

            "habit_reminder" -> {

                holder.binding.ivIcon
                    .setImageResource(
                        R.drawable.ic_fitness
                    )
            }

            "task_reminder" -> {

                holder.binding.ivIcon
                    .setImageResource(
                        R.drawable.ic_tasks
                    )
            }

            "task_overdue" -> {

                holder.binding.ivIcon
                    .setImageResource(
                        R.drawable.ic_warning
                    )
            }

            "ai_insight" -> {

                holder.binding.ivIcon
                    .setImageResource(
                        R.drawable.ic_ai
                    )
            }

            else -> {

                holder.binding.ivIcon
                    .setImageResource(
                        R.drawable.ic_notifications
                    )
            }
        }

        holder.itemView.setOnClickListener {

            onClick(item)
        }

    }

    override fun getItemCount(): Int {

        return notifications.size
    }

    fun updateData(
        newNotifications: List<Notification>
    ) {

        notifications =
            newNotifications.toMutableList()

        notifyDataSetChanged()
    }

    fun getNotification(
        position: Int
    ): Notification {

        return notifications[position]
    }

    fun removeNotification(
        position: Int
    ) {

        notifications.removeAt(position)

        notifyItemRemoved(position)
    }

    private fun getTimeAgo(
        dateTime: String
    ): String {

        return try {

            val format =
                java.text.SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    java.util.Locale.getDefault()
                )

            val date =
                format.parse(dateTime)
                    ?: return "Just now"

            val diff =
                System.currentTimeMillis() -
                        date.time

            val minutes =
                diff / (1000 * 60)

            val hours =
                minutes / 60

            val days =
                hours / 24

            when {

                minutes < 1 ->
                    "Just now"

                minutes < 60 ->
                    "${minutes}m ago"

                hours < 24 ->
                    "${hours}h ago"

                else ->
                    "${days}d ago"
            }

        } catch (e: Exception) {

            "Recently"
        }
    }
}