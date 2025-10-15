package com.example.screen_capture.data.data_source

import com.example.screen_capture.domain.model.CaptureMode
import javax.inject.Inject

interface CaptureDelayDataSource {
    fun delayMs(captureMode: CaptureMode): Long
}

class CaptureDelayDataSourceImpl @Inject constructor() : CaptureDelayDataSource {
    override fun delayMs(captureMode: CaptureMode): Long = when(captureMode) {
        CaptureMode.FPS_30 -> 33L
        CaptureMode.FPS_60 -> 16L
        CaptureMode.NO_DELAY -> 0L
    }
}
