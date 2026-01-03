package com.example.untitled.models

data class DashboardResponse(
    val success: Boolean,
    val data: DashboardData?
)

data class DashboardData(
    val total_balance: Double,
    val monthly_spending: Double,
    val recent_transactions: List<Transaction>,
    val goals_summary: List<GoalSummary>
)

data class Transaction(
    val id: String,
    val title: String,
    val amount: Double,
    val date: String,
    val type: String // "income" or "expense"
)

data class GoalSummary(
    val id: String,
    val title: String,
    val current_amount: Double,
    val target_amount: Double
)
