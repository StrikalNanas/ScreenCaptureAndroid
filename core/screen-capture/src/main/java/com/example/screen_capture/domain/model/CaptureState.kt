package com.example.screen_capture.domain.model

sealed interface CaptureState {
    data object Idle : CaptureState
    data object Capturing : CaptureState
    data class Error(val error: String) : CaptureState
}
