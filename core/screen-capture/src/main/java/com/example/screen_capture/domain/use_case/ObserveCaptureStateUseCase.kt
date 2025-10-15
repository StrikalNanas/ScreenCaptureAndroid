package com.example.screen_capture.domain.use_case

import com.example.screen_capture.domain.model.CaptureState
import com.example.screen_capture.domain.repository.CaptureRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCaptureStateUseCase @Inject constructor(
    private val captureRepository: CaptureRepository
) {
    operator fun invoke(): Flow<CaptureState> = captureRepository.observeCaptureState()
}