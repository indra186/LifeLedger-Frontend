package com.example.untitled.models

data class Task(
    val id: String,
    val title: String,
    val date: String,
    val time: String,
    val category: String,
    val is_completed: Boolean
)

data class TasksResponse(
    val success: Boolean,
    val message: String,
    val data: List<Task>?
)

data class AddTaskRequest(
    val user_id: String,
    val title: String,
    val date: String,
    val time: String,
    val category: String
)

data class AddTaskResponse(
    val success: Boolean,
    val message: String
)
