package com.example.untitled.workers

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
import com.example.untitled.R
import com.example.untitled.models.CreateNotificationRequest
import com.example.untitled.network.RetrofitClient

class TaskOverdueWorker(

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

        /*
        |--------------------------------------------------------------------------
        | CHECK COMPLETION
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
                            "Task Overdue",

                        message =
                            "You missed task: $title",

                        type =
                            "task_overdue",

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
            "task_overdue"

        val manager =
            applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        if(
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(

                    channelId,
                    "Task Overdue",
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
                    "Task Overdue"
                )

                .setContentText(
                    "You missed task: $title"
                )

                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )

                .setAutoCancel(true)
                .setGroup(
                    com.example.untitled.utils
                        .NotificationHelper
                        .GROUP_OVERDUE
                )
                .build()

        manager.notify(
            3000 + taskId,
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
                    "Overdue tasks"
                )

                .setStyle(
                    NotificationCompat.InboxStyle()
                        .setSummaryText(
                            "Overdue tasks"
                        )
                )

                .setGroup(
                    com.example.untitled.utils
                        .NotificationHelper
                        .GROUP_OVERDUE
                )

                .setGroupSummary(true)

                .build()

        manager.notify(
            9993,
            summary
        )
    }
}