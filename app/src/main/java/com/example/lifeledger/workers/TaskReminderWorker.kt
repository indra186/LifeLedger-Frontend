package com.example.lifeledger.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.lifeledger.R
import com.example.lifeledger.models.CreateNotificationRequest
import com.example.lifeledger.network.RetrofitClient
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TaskReminderWorker(

    context: Context,
    params: WorkerParameters

) : Worker(context, params) {

    override fun doWork(): Result {

        val taskId =
            inputData.getInt(
                "taskId",
                -1
            )

        val title =
            inputData.getString(
                "title"
            ) ?: "Task"

        val repeatType =
            inputData.getString(
                "repeatType"
            ) ?: "none"

        val repeatDays =
            inputData.getString(
                "repeatDays"
            ) ?: ""

        /*
        |--------------------------------------------------------------------------
        | CUSTOM DAY FILTER
        |--------------------------------------------------------------------------
        */

        if(repeatType == "weekly") {

            val currentDay =
                SimpleDateFormat(
                    "EEE",
                    Locale.getDefault()
                ).format(Calendar.getInstance().time)

            if(
                !repeatDays.contains(currentDay)
            ) {

                return Result.success()
            }
        }

        /*
        |--------------------------------------------------------------------------
        | CHECK IF ALREADY COMPLETED
        |--------------------------------------------------------------------------
        */

        try {

            val response =
                RetrofitClient.instance
                    .checkTaskCompletedToday(
                        taskId
                    )
                    .execute()

            if(
                response.isSuccessful &&
                response.body()
                    ?.data
                    ?.completed == true
            ) {

                return Result.success()
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }

        /*
        |--------------------------------------------------------------------------
        | SAVE IN-APP NOTIFICATION
        |--------------------------------------------------------------------------
        */

        try {

            RetrofitClient.instance
                .saveNotification(

                    CreateNotificationRequest(

                        title =
                            "Task Reminder",

                        message =
                            "Pending task: $title",

                        type = "task_reminder",

                        related_id =
                            taskId
                    )
                )
                .execute()

        } catch (e: Exception) {

            e.printStackTrace()
        }

        /*
        |--------------------------------------------------------------------------
        | SHOW SYSTEM NOTIFICATION
        |--------------------------------------------------------------------------
        */

        showNotification(
            taskId,
            title
        )

        return Result.success()
    }

    private fun showNotification(

        taskId: Int,
        title: String

    ) {

        val channelId =
            "task_reminders"

        val manager =
            applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel =
                NotificationChannel(

                    channelId,
                    "Task Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                )

            manager.createNotificationChannel(
                channel
            )
        }

        if(
            ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }

        val notification =
            NotificationCompat.Builder(
                applicationContext,
                channelId
            )

                .setSmallIcon(
                    R.drawable.ic_notifications
                )

                .setContentTitle(
                    "Task Reminder"
                )

                .setContentText(
                    "Pending task: $title"
                )

                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )

                .setAutoCancel(true)
                .setGroup(
                    com.example.lifeledger.utils
                        .NotificationHelper
                        .GROUP_TASKS
                )

                .build()

        manager.notify(
            taskId,
            notification
        )
        val summary =
            NotificationCompat.Builder(
                applicationContext,
                channelId
            )

                .setSmallIcon(
                    R.drawable.ic_notifications
                )

                .setContentTitle(
                    "LifeLedger"
                )

                .setContentText(
                    "Pending task reminders"
                )

                .setStyle(
                    NotificationCompat.InboxStyle()
                        .setSummaryText(
                            "Task reminders"
                        )
                )

                .setGroup(
                    com.example.lifeledger.utils
                        .NotificationHelper
                        .GROUP_TASKS
                )

                .setGroupSummary(true)

                .build()

        manager.notify(
            9991,
            summary
        )
    }
}