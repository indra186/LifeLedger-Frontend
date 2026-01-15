package com.example.untitled.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val frequency: String, // "daily", "weekly"
    val streak: Int = 0,
    val isCompletedToday: Boolean = false
)
