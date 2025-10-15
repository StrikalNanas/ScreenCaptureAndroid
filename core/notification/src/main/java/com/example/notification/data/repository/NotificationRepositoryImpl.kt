package com.example.notification.data.repository

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import com.example.notification.data.builder.NotificationBuilder
import com.example.notification.data.manager.NotificationChannelManager
import com.example.notification.domain.model.NotificationData
import com.example.notification.domain.repository.NotificationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationChannelManager: NotificationChannelManager,
    private val notificationBuilder: NotificationBuilder
) : NotificationRepository {

    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun createNotification(data: NotificationData): Notification {
        notificationChannelManager.createChannel(
            channelId = data.channelId,
            channelName = data.channelName,
            importance = data.config.priority
        )
        return notificationBuilder.buildNotification(
            title = data.title,
            content = data.content,
            channelId = data.channelId,
            config = data.config
        )
    }

    override fun showNotification(id: Int, notification: Notification) {
        notificationManager.notify(id, notification)
    }

    override fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
    }
}