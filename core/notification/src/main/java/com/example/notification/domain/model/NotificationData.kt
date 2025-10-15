package com.example.notification.domain.model

data class NotificationData(
    val id: Int,
    val title: String,
    val content: String,
    val channelId: String,
    val channelName: String,
    val config: NotificationConfig
)
