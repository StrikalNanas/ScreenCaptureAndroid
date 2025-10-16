package com.example.screen_capture.domain.repository

import android.media.projection.MediaProjection
import com.example.screen_capture.domain.model.CaptureFrame
import com.example.screen_capture.domain.model.CaptureMode
import com.example.screen_capture.domain.model.CaptureState
import kotlinx.coroutines.flow.Flow

interface CaptureRepository {
    fun observeCaptureState(): Flow<CaptureState>
    suspend fun startCapture(
        mediaProjection: MediaProjection,
        captureMode: CaptureMode
    )
    suspend fun stopCapture()
    fun observeFrame(): Flow<CaptureFrame?>
    fun getLastFrame(): CaptureFrame?
}