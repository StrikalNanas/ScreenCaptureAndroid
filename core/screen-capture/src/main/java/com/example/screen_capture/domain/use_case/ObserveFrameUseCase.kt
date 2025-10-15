package com.example.screen_capture.domain.use_case

import com.example.screen_capture.domain.model.CaptureFrame
import com.example.screen_capture.domain.repository.CaptureRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFrameUseCase @Inject constructor(
    private val captureRepository: CaptureRepository
) {
    operator fun invoke(): Flow<CaptureFrame?> = captureRepository.observeFrame()
}