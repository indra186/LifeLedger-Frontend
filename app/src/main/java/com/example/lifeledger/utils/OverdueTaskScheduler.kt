package com.example.lifeledger.utils

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.lifeledger.models.Task
import com.example.lifeledger.workers.TaskOverdueWorker
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

object OverdueTaskScheduler {

    fun scheduleOverdueReminder(

        context: Context,
        task: Task

    ) {

        if(
            task.completed == 1 ||
            task.reminder_enabled != 1
        ) {
            return
        }

        if(
            task.date.isNullOrEmpty() ||
            task.time.isNullOrEmpty()
        ) {
            return
        }

        try {

            val formatter =
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                )

            val dueDateTime =
                "${task.date} ${task.time}"

            val dueMillis =
                formatter.parse(
                    dueDateTime
                )?.time ?: return

            /*
            |--------------------------------------------------------------------------
            | OVERDUE AFTER 1 HOUR
            |--------------------------------------------------------------------------
            */

            val overdueMillis =
                dueMillis +
                        (60 * 60 * 1000)

            val delay =
                overdueMillis -
                        System.currentTimeMillis()

            if(delay <= 0)
                return

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

                    .build()

            val request =
                OneTimeWorkRequestBuilder<
                        TaskOverdueWorker
                        >()

                    .setInitialDelay(
                        delay,
                        TimeUnit.MILLISECONDS
                    )

                    .setInputData(data)

                    .build()

            WorkManager
                .getInstance(context)
                .enqueue(request)

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }
}