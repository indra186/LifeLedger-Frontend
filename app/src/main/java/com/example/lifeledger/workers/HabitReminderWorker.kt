package com.example.lifeledger.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.lifeledger.R
import com.example.lifeledger.models.CreateNotificationRequest
import com.example.lifeledger.network.RetrofitClient

class HabitReminderWorker(

    context: Context,
    params: WorkerParameters

) : Worker(context, params) {

    override fun doWork(): Result {

        val habitId =
            inputData.getInt(
                "habitId",
                -1
            )

        val habitName =
            inputData.getString(
                "habitName"
            ) ?: "Habit"

        val frequency =
            inputData.getString(
                "frequency"
            ) ?: "daily"

        val selectedDays =
            inputData.getString(
                "selectedDays"
            ) ?: ""

        val todayDay =
            java.text.SimpleDateFormat(
                "EEE",
                java.util.Locale.getDefault()
            ).format(
                java.util.Date()
            )

        /*
        |--------------------------------------------------------------------------
        | SKIP IF CUSTOM DAY DOES NOT MATCH
        |--------------------------------------------------------------------------
        */

        if (
            frequency == "custom" &&
            !selectedDays.contains(todayDay)
        ) {

            return Result.success()
        }

        try {

            val response =
                RetrofitClient.instance
                    .checkHabitCompletedToday(
                        habitId
                    )
                    .execute()

            /*
            |--------------------------------------------------------------------------
            | IF ALREADY COMPLETED
            |--------------------------------------------------------------------------
            */

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
        | SHOW NOTIFICATION
        |--------------------------------------------------------------------------
        */
        try {

            RetrofitClient.instance
                .saveNotification(

                    CreateNotificationRequest(

                        title =
                            "Habit Reminder",

                        message =
                            "Time to complete: $habitName",
                        type = "habit_reminder",

                        related_id =
                            habitId
                    )
                )
                .execute()

        } catch (e: Exception) {

            e.printStackTrace()
        }

        showNotification(
            habitId,
            habitName
        )

        return Result.success()
    }

    private fun showNotification(

        habitId: Int,
        habitName: String

    ) {

        val channelId =
            "habit_reminders"

        val manager =
            applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        /*
        |--------------------------------------------------------------------------
        | CHANNEL
        |--------------------------------------------------------------------------
        */

        if (
            android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(
                    channelId,
                    "Habit Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                )

            manager.createNotificationChannel(
                channel
            )
        }


        /*
        |--------------------------------------------------------------------------
        | PERMISSION CHECK
        |--------------------------------------------------------------------------
        */

        if(
            ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }

        /*
        |--------------------------------------------------------------------------
        | NOTIFICATION
        |--------------------------------------------------------------------------
        */

        val notification =
            NotificationCompat.Builder(
                applicationContext,
                channelId
            )

                .setSmallIcon(
                    R.drawable.ic_notifications
                )

                .setContentTitle(
                    "Habit Reminder"
                )

                .setContentText(
                    "Time to complete: $habitName"
                )

                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )

                .setAutoCancel(true)
                .setGroup(
                    com.example.lifeledger.utils
                        .NotificationHelper
                        .GROUP_HABITS
                )

                .build()

        manager.notify(
            habitId,
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
                    "Habit reminders"
                )

                .setStyle(
                    NotificationCompat.InboxStyle()
                        .setSummaryText(
                            "Habit reminders"
                        )
                )

                .setGroup(
                    com.example.lifeledger.utils
                        .NotificationHelper
                        .GROUP_HABITS
                )

                .setGroupSummary(true)

                .build()

        manager.notify(
            9992,
            summary
        )
    }

}