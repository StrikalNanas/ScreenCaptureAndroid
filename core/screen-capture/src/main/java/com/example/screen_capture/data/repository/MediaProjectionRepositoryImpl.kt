package com.example.screen_capture.data.repository

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import com.example.screen_capture.domain.repository.MediaProjectionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MediaProjectionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MediaProjectionRepository {

    private val mediaProjectionManager by lazy {
        context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    override fun getMediaProjectionIntent(): Intent {
        return mediaProjectionManager.createScreenCaptureIntent()
    }

    override fun getMediaProjection(resultCode: Int, data: Intent): MediaProjection? {
        return mediaProjectionManager.getMediaProjection(resultCode, data)
    }
}