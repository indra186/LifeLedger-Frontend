package com.example.untitled.models

data class DashboardResponse(
    val success: Boolean,
    val message: String,
    val data: DashboardData
)

data class DashboardData(
    val name: String,
    val total_balance: Double,
    val recent_transactions: List<Transaction>,
    val active_habits: List<DashboardHabit>,
    val goals: List<DashboardGoal>
)



data class Transaction(
    val id: Int,
    val amount: Double,
    val type: String,
    val category: String,
    val description: String,
    val tx_date: String
)

data class DashboardHabit(
    val id: Int,
    val name: String,
    val goal_per_day: Int
)

data class DashboardGoal(
    val id: Int,
    val title: String,
    val target_amount: Double,
    val current_amount: Double
)


