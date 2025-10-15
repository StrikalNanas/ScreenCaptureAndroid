package com.example.screen_capture.domain.use_case

import android.content.Intent
import com.example.screen_capture.domain.repository.MediaProjectionRepository
import javax.inject.Inject

class GetMediaProjectionIntentUseCase @Inject constructor(
    private val mediaProjectionRepository: MediaProjectionRepository
) {
    operator fun invoke(): Intent = mediaProjectionRepository.getMediaProjectionIntent()
}