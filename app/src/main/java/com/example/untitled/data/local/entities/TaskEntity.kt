package com.example.untitled.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val date: String?,
    val time: String?,
    val isCompleted: Boolean = false,
    val description: String = "",

    val repeatType: String = "none",

    val repeatDays: String? = null,

    val priority: String = "medium",

    val reminderEnabled: Boolean = false,

    val attachmentUri: String? = null,
)
