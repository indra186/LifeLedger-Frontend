package com.example.untitled.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val deadline: String?,
    val status: String = "active"
)
