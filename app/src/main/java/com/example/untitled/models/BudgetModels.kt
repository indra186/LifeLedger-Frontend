package com.example.untitled.models
import com.google.gson.annotations.SerializedName

data class Budget(
    val id: String,
    val category: String,
    val limit_amount: String,
    val alert_threshold: String,
    val period: String,
    val spent_amount: String
)

data class BudgetsResponse(
    val success: Boolean,
    val message: String,
    val data: List<Budget>
)

data class CreateBudgetRequest(
    val category: String,
    val limit_amount: Double,
    val period: String
)

data class CreateBudgetResponse(
    val success: Boolean,
    val message: String,
    val budget_id: String?
)
