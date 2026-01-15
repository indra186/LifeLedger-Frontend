package com.example.untitled.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_metrics")
data class HealthMetricEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // e.g., "Steps", "Heart Rate", "Water"
    val value: Double,
    val unit: String, // e.g., "steps", "bpm", "ml"
    val date: String,
    val time: String? = null
)
