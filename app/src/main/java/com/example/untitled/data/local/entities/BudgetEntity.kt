package com.example.untitled.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val limitAmount: Double,
    val spentAmount: Double = 0.0,
    val period: String = "monthly"
)
