package com.example.lifeledger.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.lifeledger.R

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {

        val title =
            inputData.getString("title")
                ?: "Reminder"

        val message =
            inputData.getString("message")
                ?: "You have a pending reminder"

        createNotificationChannel()

        showNotification(
            title,
            message
        )

        return Result.success()
    }

    private fun showNotification(
        title: String,
        message: String
    ) {

        val builder =
            NotificationCompat.Builder(
                applicationContext,
                "lifeledger_reminders"
            )
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setAutoCancel(true)

        if (
            ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        NotificationManagerCompat
            .from(applicationContext)
            .notify(
                System.currentTimeMillis().toInt(),
                builder.build()
            )
    }

    private fun createNotificationChannel() {

        if (
            android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(
                    "lifeledger_reminders",
                    "LifeLedger Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                )

            channel.description =
                "Task and Habit reminders"

            val manager =
                applicationContext.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            manager.createNotificationChannel(
                channel
            )
        }
    }
}