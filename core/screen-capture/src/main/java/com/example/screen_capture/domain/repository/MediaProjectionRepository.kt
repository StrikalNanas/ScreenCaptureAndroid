package com.example.screen_capture.domain.repository

import android.content.Intent
import android.media.projection.MediaProjection

interface MediaProjectionRepository {
    fun getMediaProjectionIntent(): Intent
    fun getMediaProjection(
        resultCode: Int,
        data: Intent
    ): MediaProjection?
}