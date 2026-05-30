package com.example.lifeledger.models

import com.google.gson.annotations.SerializedName

data class Goal(
    val id: String,
    val title: String,
    val target_amount: Double,
    val current_amount: Double,
    @SerializedName("target_date")
    val deadline: String?,
    @SerializedName("created_at")
    val created_at: String?,
    val status: String
)

data class GoalsResponse(
    val success: Boolean,
    val message: String,
    val data: GoalsData
)
data class GoalDetailResponse(
    val success: Boolean,
    val message: String,
    val data: Goal
)

data class GoalsData(
    val goals: List<Goal>
)

data class AddGoalRequest(
    val user_id: String,
    val title: String,
    val target_amount: Double,
    @SerializedName("target_date")
    val deadline: String?,
    val current_amount: Double?
)
data class AddGoalProgressRequest(
    val goal_id: Int,
    val amount: Double,
    val account_id: Int
)

data class AddGoalResponse(
    val success: Boolean,
    val message: String,
    val goal_id: String?
)
data class GoalHistoryResponse(
    val success: Boolean,
    val data: GoalHistoryData
)

data class GoalHistoryData(
    val goal_id: Int,
    val progress: List<GoalProgress>
)

data class GoalProgress(
    val id: Int,
    val amount_added: Double,
    val date_added: String
)
data class DeleteGoalRequest(
    val goal_id: Int
)
data class GoalProgressResponse(
    val success: Boolean,
    val message: String,
    val data: GoalProgressData?
)

data class GoalProgressData(
    val used_amount: Double,
    val extra_amount: Double
)
data class GoalInsight(
    val goalId: String,
    val state: GoalState,
    val requiredPerDay: Double,
    val actualPerDay: Double,
    val gap: Double,
    val recoveryDays: Int,
    val daysLeft: Int,
    val remaining: Double,
    val consistency: Double,
    val trend: Trend,
    val willMiss: Boolean,
    val achievable: Boolean
)
enum class Trend {
    UP, DOWN, STABLE
}
enum class GoalState {
    COMPLETED,
    OVERDUE,
    URGENT,      // 0–3 days
    SHORT_TERM,  // 4–7 days
    NORMAL
}
enum class ConfidenceLevel {
    HIGH, MEDIUM, LOW
}
data class MultiGoalInsight(
    val prioritizedGoals: List<GoalInsight>,
    val allocation: Map<GoalInsight, Double>,
    val totalCapacity: Double
)