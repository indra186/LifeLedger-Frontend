package com.example.lifeledger.utils

import android.content.Context
import androidx.work.*
import com.example.lifeledger.models.Habit
import com.example.lifeledger.workers.HabitReminderWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

object HabitReminderScheduler {

    fun scheduleHabitReminder(
        context: Context,
        habit: Habit
    ) {

        val reminder =
            habit.reminder_time
                ?: return

        val parts =
            reminder.split(":")

        val hour =
            parts[0].toInt()

        val minute =
            parts[1].toInt()

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

        val initialDelay =
            calendar.timeInMillis -
                    System.currentTimeMillis()

        val data =
            Data.Builder()
                .putInt(
                    "habitId",
                    habit.id
                )
                .putString(
                    "habitName",
                    habit.name
                )
                .putString(
                    "frequency",
                    habit.frequency
                )
                .putString(
                    "selectedDays",
                    habit.selected_days ?: ""
                )
                .build()

        val request =
            PeriodicWorkRequestBuilder<
                    HabitReminderWorker
                    >(
                24,
                TimeUnit.HOURS
            )
                .setInitialDelay(
                    initialDelay,
                    TimeUnit.MILLISECONDS
                )
                .setInputData(data)
                .build()

        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(
                "habit_${habit.id}",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
    }
}
