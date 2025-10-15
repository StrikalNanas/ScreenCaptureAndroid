package com.example.notification.data.builder

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import android.app.NotificationManager
import com.example.notification.domain.model.NotificationConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface NotificationBuilder {
    fun buildNotification(
        title: String,
        content: String,
        channelId: String,
        config: NotificationConfig
    ): Notification
}

class NotificationBuilderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NotificationBuilder {
    override fun buildNotification(
        title: String,
        content: String,
        channelId: String,
        config: NotificationConfig
    ): Notification {
        val compatPriority = when (config.priority) {
            NotificationManager.IMPORTANCE_MIN -> NotificationCompat.PRIORITY_MIN
            NotificationManager.IMPORTANCE_LOW -> NotificationCompat.PRIORITY_LOW
            NotificationManager.IMPORTANCE_DEFAULT -> NotificationCompat.PRIORITY_DEFAULT
            NotificationManager.IMPORTANCE_HIGH, NotificationManager.IMPORTANCE_MAX -> NotificationCompat.PRIORITY_HIGH
            else -> NotificationCompat.PRIORITY_DEFAULT
        }

        return NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(config.smallIcon)
            .setOngoing(config.isOngoing)
            .setPriority(compatPriority)
            .setAutoCancel(config.autoCancel)
            .apply {
                if (!config.sound) setSound(null)
                if (!config.vibrate) setVibrate(null)
            }
            .build()
    }
}