package com.example.lifeledger.models

data class Notification(

    val id: Int,

    val title: String,

    val message: String,

    val type: String,

    val related_id: Int?,

    val is_read: Int,

    val created_at: String
)
data class NotificationResponse(

    val success: Boolean,

    val message: String,

    val data: List<Notification>
)
data class CreateNotificationRequest(

    val title: String,

    val message: String,

    val type: String,

    val related_id: Int?
)
data class DeleteNotificationRequest(

    val notification_id: Int
)