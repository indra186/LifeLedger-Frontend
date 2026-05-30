package com.example.lifeledger.models

data class FinanceInsight(
    val monthlyIncome: Double,
    val monthlyExpense: Double,
    val surplus: Double,
    val topCategory: String,
    val topCategorySpend: Double,
    val isOverspending: Boolean,
    val savingCapacity: Double
)