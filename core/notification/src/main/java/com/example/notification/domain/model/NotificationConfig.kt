package com.example.notification.domain.model

import android.app.NotificationManager
import androidx.annotation.DrawableRes

data class NotificationConfig(
    @DrawableRes val smallIcon: Int,
    val isOngoing: Boolean,
    val priority: Int = NotificationManager.IMPORTANCE_MAX,
    val autoCancel: Boolean = false,
    val vibrate: Boolean = false,
    val sound: Boolean = false
)