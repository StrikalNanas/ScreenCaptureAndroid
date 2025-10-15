package com.example.screen_capture.domain.use_case

import com.example.screen_capture.domain.model.CaptureFrame
import com.example.screen_capture.domain.repository.CaptureRepository
import javax.inject.Inject

class GetLastFrameUseCase @Inject constructor(
    private val captureRepository: CaptureRepository
) {
    operator fun invoke(): CaptureFrame? = captureRepository.getLastFrame()
}