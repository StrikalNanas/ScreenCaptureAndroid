package com.example.notification.domain.repository

import android.app.Notification
import com.example.notification.domain.model.NotificationData

interface NotificationRepository {
    fun createNotification(data: NotificationData): Notification
    fun showNotification(id: Int, notification: Notification)
    fun cancelNotification(id: Int)
}