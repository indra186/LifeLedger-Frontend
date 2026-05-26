package com.example.untitled.models

data class MonthlyReportResponse(

    val success: Boolean,

    val message: String,

    val data: MonthlyReportData
)

data class MonthlyReportData(

    val summary: Summary,

    val categories: List<CategorySpending>,

    val spending_pattern: List<SpendingPattern>,

    val is_current_month: Boolean
)

data class Summary(

    val income: Double,

    val expense: Double,

    val savings: Double,

    val top_category: String
)

data class CategorySpending(

    val category: String,

    val total: Double,

    val budget_limit: Double,

    val usage_percent: Int
)

data class SpendingPattern(

    val label: String,

    val total: Double
)