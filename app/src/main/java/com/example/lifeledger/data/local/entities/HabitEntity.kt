package com.example.lifeledger.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val description: String = "",

    val icon: String = "fitness",

    val frequency: String,
    val selectedDays: String = "",

    val goalPerDay: Int = 1,

    val goalUnit: String = "times",

    val reminderTime: String? = null,

    val streak: Int = 0,

    val isCompletedToday: Boolean = false
)