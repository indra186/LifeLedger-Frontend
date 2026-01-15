package com.example.untitled.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val date: String?,
    val time: String?,
    val category: String,
    val isCompleted: Boolean = false
)
