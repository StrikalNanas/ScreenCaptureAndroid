package com.example.screen_capture.presentation.screens

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.screen_capture.domain.model.CaptureFrame
import com.example.screen_capture.domain.model.CaptureMode
import com.example.screen_capture.domain.model.CaptureState
import com.example.screen_capture.domain.use_case.GetMediaProjectionIntentUseCase
import com.example.screen_capture.domain.use_case.ObserveCaptureStateUseCase
import com.example.screen_capture.domain.use_case.ObserveFrameUseCase
import com.example.screen_capture.service.CaptureService
import com.example.screen_capture.service.CaptureService.Companion.ACTION_START
import com.example.screen_capture.service.CaptureService.Companion.ACTION_STOP
import com.example.screen_capture.service.CaptureService.Companion.EXTRA_CAPTURE_MODE
import com.example.screen_capture.service.CaptureService.Companion.EXTRA_DATA
import com.example.screen_capture.service.CaptureService.Companion.EXTRA_RESULT_CODE
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CaptureViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getMediaProjectionIntentUseCase: GetMediaProjectionIntentUseCase,
    private val observeCaptureStateUseCase: ObserveCaptureStateUseCase,
    private val observeFrameUseCase: ObserveFrameUseCase
) : ViewModel() {

    private val _mediaProjectionIntent = MutableStateFlow<Intent?>(null)
    val mediaProjectionIntent: StateFlow<Intent?> = _mediaProjectionIntent.asStateFlow()

    private val _captureMode: MutableStateFlow<CaptureMode> = MutableStateFlow(CaptureMode.FPS_60)
    val captureMode: StateFlow<CaptureMode> = _captureMode

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

    fun setCaptureMode(mode: CaptureMode) {
        _captureMode.value = mode
    }

    fun startCapture(
        resultCode: Int,
        data: Intent
    ) {
        val intent = Intent(context, CaptureService::class.java).apply {
            action = ACTION_START
            putExtra(EXTRA_CAPTURE_MODE, _captureMode.value.ordinal)
            putExtra(EXTRA_RESULT_CODE, resultCode)
            putExtra(EXTRA_DATA, data)
        }
        context.startService(intent)
    }

    fun stopCapture() {
        val intent = Intent(context, CaptureService::class.java).apply {
            action = ACTION_STOP
        }
        context.startService(intent)
    }
}