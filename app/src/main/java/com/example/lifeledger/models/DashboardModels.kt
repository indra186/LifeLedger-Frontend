package com.example.lifeledger.models

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
    val goals: List<DashboardGoal>,

    val monthly_expense: Double,
    val monthly_transaction_count: Int,
    val today_habits_total: Int,
    val today_habits_completed: Int,
    val balance_change_percent: Double,
    val balance_change_positive: Boolean,
    val balance_change_amount: Double,
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


