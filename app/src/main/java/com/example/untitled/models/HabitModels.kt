package com.example.untitled.models

data class Habit(
    val id: Int,
    val name: String,
    val description: String,
    val icon: String,
    val frequency: String,
    val selected_days: String?,
    val goal_per_day: Int,
    val goal_unit: String,
    val reminder_time: String?,
    val completed_today: Int,
    val streak: Int
)
data class HabitsResponse(
    val success: Boolean,
    val message: String,
    val data: List<Habit>?
)

data class CreateHabitRequest(
    val habit_name: String,
    val description: String,
    val icon: String,
    val frequency: String,
    val selected_days: String,
    val goal_per_day: Int,
    val goal_unit: String,
    val reminder_time: String?
)

data class CreateHabitResponse(
    val success: Boolean,
    val message: String,
    val data: HabitIdData?
)

data class HabitIdData(
    val id: Int
)

data class CheckHabitRequest(
    val habit_id: String,
    val date: String // YYYY-MM-DD
)

data class CheckHabitResponse(
    val success: Boolean,
    val message: String
)
