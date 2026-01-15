package com.example.untitled.models

data class Habit(
    val id: String,
    val title: String,
    val frequency: String, // e.g., "daily", "weekly"
    val completed_today: Boolean,
    val streak: Int
)

data class HabitsResponse(
    val success: Boolean,
    val message: String,
    val data: List<Habit>?
)

data class CreateHabitRequest(
    val user_id: String,
    val title: String,
    val frequency: String
)

data class CreateHabitResponse(
    val success: Boolean,
    val message: String,
    val habit_id: String?
)

data class CheckHabitRequest(
    val habit_id: String,
    val date: String // YYYY-MM-DD
)

data class CheckHabitResponse(
    val success: Boolean,
    val message: String
)
