package com.example.screen_capture.data.data_source

import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.os.Handler
import android.view.Surface
import javax.inject.Inject

interface VirtualDisplayDataSource {
    fun createVirtualDisplay(
        mediaProjection: MediaProjection,
        width: Int,
        height: Int,
        densityDpi: Int,
        surface: Surface,
        handler: Handler
    ): VirtualDisplay?
}

class VirtualDisplayDataSourceImpl @Inject constructor() : VirtualDisplayDataSource {
    override fun createVirtualDisplay(
        mediaProjection: MediaProjection,
        width: Int,
        height: Int,
        densityDpi: Int,
        surface: Surface,
        handler: Handler
    ): VirtualDisplay? {
        return mediaProjection.createVirtualDisplay(
            "ScreenCapture",
            width,
            height,
            densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
            surface,
            null,
            handler
        )
    }
}