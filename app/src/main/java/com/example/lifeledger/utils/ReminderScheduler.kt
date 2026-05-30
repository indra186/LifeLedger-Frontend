package com.example.lifeledger.utils

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.lifeledger.workers.ReminderWorker
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    fun scheduleReminder(
        context: Context,
        title: String,
        message: String,
        triggerTimeMillis: Long
    ) {

        val delay =
            triggerTimeMillis -
                    System.currentTimeMillis()

        if(delay <= 0)
            return

        val data =
            Data.Builder()
                .putString("title", title)
                .putString("message", message)
                .build()

        val request =
            OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInputData(data)
                .setInitialDelay(
                    delay,
                    TimeUnit.MILLISECONDS
                )
                .build()

        WorkManager
            .getInstance(context)
            .enqueue(request)
    }
}