package com.example.untitled.models

data class Task(

    val id: String,

    val title: String,

    val description: String?,

    val date: String?,

    val time: String?,

    val priority: String?,

    val repeat_type: String?,

    val repeat_days: String?,

    val reminder_enabled: Int?,

    val attachment_uri: String?,

    val completed: Int
)

data class TasksResponse(

    val success: Boolean,

    val message: String,

    val data: List<Task>?
)

data class CreateTaskRequest(

    val title: String,

    val description: String,

    val date: String,

    val time: String,

    val priority: String,

    val repeat_type: String,

    val repeat_days: String?,

    val reminder_enabled: Boolean,

    val attachment_uri: String?
)

data class CreateTaskResponse(

    val success: Boolean,

    val message: String
)
data class TaskSection(

    val title: String,

    val tasks: List<Task>,

    var expanded: Boolean = true
)
data class UpdateTaskStatusRequest(

    val task_id: String,

    val completed: Int
)
data class DeleteTaskRequest(

    val task_id: String
)