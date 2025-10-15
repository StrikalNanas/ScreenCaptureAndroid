package com.example.screen_capture.presentation.screens

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.screen_capture.domain.model.CaptureFrame
import com.example.screen_capture.domain.model.CaptureState
import com.example.screen_capture.domain.use_case.GetMediaProjectionIntentUseCase
import com.example.screen_capture.domain.use_case.ObserveCaptureStateUseCase
import com.example.screen_capture.domain.use_case.ObserveFrameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CaptureViewModel @Inject constructor(
    private val getMediaProjectionIntentUseCase: GetMediaProjectionIntentUseCase,
    private val observeCaptureStateUseCase: ObserveCaptureStateUseCase,
    private val observeFrameUseCase: ObserveFrameUseCase
) : ViewModel() {

    private val _mediaProjectionIntent = MutableStateFlow<Intent?>(null)
    val mediaProjectionIntent: StateFlow<Intent?> = _mediaProjectionIntent.asStateFlow()

    val captureState: StateFlow<CaptureState> = observeCaptureStateUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CaptureState.Idle
        )

    val screenFrame: StateFlow<CaptureFrame?> = observeFrameUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun requestMediaProjection() {
        _mediaProjectionIntent.value = getMediaProjectionIntentUseCase()
    }

    }
}