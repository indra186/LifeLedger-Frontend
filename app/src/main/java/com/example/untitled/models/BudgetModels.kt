package com.example.untitled.models
import com.google.gson.annotations.SerializedName

data class Budget(
    val id: String,
    val category: String,
    val limit_amount: String,
    val alert_threshold: String,
    val period: String,
    val spent_amount: String,
    val created_at: String,
    val month: Int,
    val year: Int
)

data class BudgetsResponse(
    val success: Boolean,
    val message: String,
    val data: List<Budget>
)
data class CreateBudgetRequest(
    val category: String,
    val limit_amount: Double,
    val alert_enabled: Boolean,
    val month: Int,
    val year: Int
)

data class CreateBudgetResponse(
    val success: Boolean,
    val message: String,
    val budget_id: String?
)
data class AvailableMonth(
    val month: Int,
    val year: Int
)

data class AvailableMonthsResponse(
    val success: Boolean,
    val message: String,
    val data: List<AvailableMonth>
)
data class BudgetTransaction(
    val id: String,
    val category: String,
    val amount: String,
    val description: String,
    val tx_date: String
)

data class BudgetTransactionResponse(
    val success: Boolean,
    val message: String,
    val data: List<BudgetTransaction>
)
