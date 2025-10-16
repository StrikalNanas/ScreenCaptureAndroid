package com.example.screen_capture.service

import android.app.Activity
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.example.screen_capture.domain.repository.CaptureRepository
import com.example.screen_capture.domain.repository.MediaProjectionRepository
import com.example.notification.domain.model.NotificationConfig
import com.example.notification.domain.model.NotificationData
import com.example.notification.domain.repository.NotificationRepository
import com.example.screen_capture.domain.model.CaptureMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CaptureService : Service() {

    companion object {
        const val ACTION_START = "ACTION_START_CAPTURE"
        const val ACTION_STOP = "ACTION_STOP_CAPTURE"
        const val EXTRA_CAPTURE_MODE = "extra_capture_mode"
        const val EXTRA_RESULT_CODE = "extra_result_code"
        const val EXTRA_DATA = "extra_data"
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "capture_channel"
        const val CHANNEL_NAME = "Screen Capture"
    }

    @Inject
    lateinit var notificationRepository: NotificationRepository
    @Inject
    lateinit var mediaProjectionRepository: MediaProjectionRepository
    @Inject
    lateinit var captureRepository: CaptureRepository

    private val captureJob = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val notificationData = NotificationData(
        id = NOTIFICATION_ID,
        title = "Screen Capture",
        content = "Capturing screen...",
        channelId = CHANNEL_ID,
        channelName = CHANNEL_NAME,
        config = NotificationConfig(
            smallIcon = com.example.notification.R.drawable.ic_launcher_background,
            isOngoing = true,
            priority = NotificationManager.IMPORTANCE_MAX,
            autoCancel = false,
            vibrate = false,
            sound = false,
        )
    )
    
    override fun onCreate() {
        super.onCreate()
        val notification = notificationRepository.createNotification(notificationData)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when(intent.action) {
            ACTION_START -> {
                val resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, Activity.RESULT_CANCELED)
                val data = intent.getParcelableExtra<Intent>(EXTRA_DATA)
                val modeOrdinal = intent.getIntExtra(EXTRA_CAPTURE_MODE, CaptureMode.FPS_60.ordinal)
                val captureMode = CaptureMode.entries[modeOrdinal]
                val mediaProjection = mediaProjectionRepository.getMediaProjection(
                    resultCode = resultCode,
                    data = data ?: return START_STICKY
                )
                captureJob.launch {
                    mediaProjection?.let { captureRepository.startCapture(it, captureMode) }
                }
            }
            ACTION_STOP -> {
                captureJob.launch {
                    captureRepository.stopCapture()
                    stopSelf()
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationRepository.cancelNotification(NOTIFICATION_ID)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}