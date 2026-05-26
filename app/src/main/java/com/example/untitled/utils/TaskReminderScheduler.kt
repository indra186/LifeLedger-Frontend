package com.example.untitled.utils

import android.content.Context
import androidx.work.*
import com.example.untitled.models.Task
import com.example.untitled.workers.TaskReminderWorker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

object TaskReminderScheduler {

    fun scheduleTaskReminder(
        context: Context,
        task: Task
    ) {

        if(task.reminder_enabled != 1)
            return
        if(task.completed == 1)
            return

        val taskTime =
            task.time ?: return

        val repeatType =
            task.repeat_type ?: "none"

        val timeParts =
            taskTime.split(":")

        if(timeParts.size < 2)
            return

        val hour =
            timeParts[0].toInt()

        val minute =
            timeParts[1].toInt()

        val calendar =
            Calendar.getInstance()

        calendar.set(
            Calendar.HOUR_OF_DAY,
            hour
        )

        calendar.set(
            Calendar.MINUTE,
            minute
        )

        calendar.set(
            Calendar.SECOND,
            0
        )

        if(
            calendar.timeInMillis <
            System.currentTimeMillis()
        ) {

            calendar.add(
                Calendar.DAY_OF_MONTH,
                1
            )
        }

        val delay =
            calendar.timeInMillis -
                    System.currentTimeMillis()

        val data =
            Data.Builder()

                .putInt(
                    "taskId",
                    task.id
                )

                .putString(
                    "title",
                    task.title
                )

                .putString(
                    "repeatType",
                    repeatType
                )

                .putString(
                    "repeatDays",
                    task.repeat_days ?: ""
                )

                .build()

        val request =
            PeriodicWorkRequestBuilder<
                    TaskReminderWorker
                    >(
                24,
                TimeUnit.HOURS
            )

                .setInitialDelay(
                    delay,
                    TimeUnit.MILLISECONDS
                )

                .setInputData(data)

                .build()

        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(

                "task_${task.id}",

                ExistingPeriodicWorkPolicy.UPDATE,

                request
            )
    }
}