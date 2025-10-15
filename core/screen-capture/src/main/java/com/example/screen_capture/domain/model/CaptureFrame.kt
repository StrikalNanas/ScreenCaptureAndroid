package com.example.screen_capture.domain.model

import android.graphics.Bitmap

data class CaptureFrame(
    val bitmap: Bitmap,
    val timestamp: Long = System.currentTimeMillis()
)
