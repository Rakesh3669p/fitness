package com.gym.gymapp.ui.noification.model

data class NotificationModel(
    val data: List<NotificationData>,
    val success: Boolean,
    val total_new_notification: Int
)

data class NotificationData(
    val is_read: String,
    val message: String,
    val notification_id: String,
    val title: String
)