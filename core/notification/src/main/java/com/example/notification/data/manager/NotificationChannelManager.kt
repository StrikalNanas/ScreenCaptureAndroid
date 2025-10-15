package com.example.notification.data.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface NotificationChannelManager {
    fun createChannel(
        channelId: String,
        channelName: String,
        importance: Int
    )
    fun deleteChannel(channelId: String)
    fun channelExists(channelId: String): Boolean
}

class NotificationChannelManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NotificationChannelManager {

    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun createChannel(channelId: String, channelName: String, importance: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!channelExists(channelId)) {
                val notificationChannel = NotificationChannel(channelId, channelName, importance)
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }
    }

    override fun deleteChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(channelId)
        }
    }

    override fun channelExists(channelId: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return notificationManager.getNotificationChannel(channelId) != null
        }
        return false
    }
}