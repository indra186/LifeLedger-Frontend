package com.example.untitled.models

data class Goal(
    val id: String,
    val title: String,
    val target_amount: Double,
    val current_amount: Double,
    val deadline: String?,
    val status: String
)

data class GoalsResponse(
    val success: Boolean,
    val message: String,
    val data: List<Goal>?
)

data class AddGoalRequest(
    val user_id: String,
    val title: String,
    val target_amount: Double,
    val deadline: String?,
    val initial_amount: Double?
)

data class AddGoalResponse(
    val success: Boolean,
    val message: String,
    val goal_id: String?
)
