package com.example.untitled.models

data class OpenAIRequest(
    val goal: GoalPayload,
    val finance: FinancePayload,
    val recovery: RecoveryPayload
)
data class GoalPayload(
    var id: String,
    val required: Double,
    val actual: Double,
    val remaining: Double,
    val daysLeft: Int
)
data class FinancePayload(
    val income: Double,
    val expense: Double,
    val topCategory: String,
    val surplus: Double
)
data class RecoveryPayload(
    val suggestion: String
)

data class OpenAIResponse(
    val success: Boolean,
    val data: AgentAction?
)