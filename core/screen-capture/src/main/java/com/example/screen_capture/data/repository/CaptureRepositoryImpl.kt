package com.example.screen_capture.data.repository

import android.graphics.Bitmap
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.os.HandlerThread
import com.example.screen_capture.data.data_source.ImageReaderDataSource
import com.example.screen_capture.data.data_source.ScreenMetricsDataSource
import com.example.screen_capture.data.data_source.VirtualDisplayDataSource
import com.example.screen_capture.domain.model.CaptureFrame
import com.example.screen_capture.domain.model.CaptureState
import com.example.screen_capture.domain.repository.CaptureRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class CaptureRepositoryImpl @Inject constructor(
    private val screenMetricsDataSource: ScreenMetricsDataSource,
    private val imageReaderDataSource: ImageReaderDataSource,
    private val virtualDisplayDataSource: VirtualDisplayDataSource
) : CaptureRepository {

    private val _captureState: MutableStateFlow<CaptureState> = MutableStateFlow(CaptureState.Idle)
    private val lastFrameRef = AtomicReference<CaptureFrame?>(null)
    private val _frame: MutableSharedFlow<CaptureFrame?> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private var handlerThread: HandlerThread? = null
    private var handler: Handler? = null

    private var mediaProjection: MediaProjection? = null
    private var mediaProjectionCallBack: MediaProjection.Callback? = null
    private var imageReader: ImageReader? = null
    private var virtualDisplay: VirtualDisplay? = null

    private val scope = CoroutineScope(Dispatchers.Default)
    private var captureJob: Job? = null

    override fun observeCaptureState(): Flow<CaptureState> = _captureState.asStateFlow()

    override suspend fun startCapture(mediaProjection: MediaProjection) = withContext(Dispatchers.Default) {
        if (_captureState.value == CaptureState.Capturing) return@withContext
        try {
            this@CaptureRepositoryImpl.mediaProjection = mediaProjection
            _captureState.value = CaptureState.Capturing

            handlerThread = HandlerThread("ScreenCapture").also { it.start() }
            handler = Handler(handlerThread!!.looper)

            mediaProjectionCallBack = object : MediaProjection.Callback() {
                override fun onStop() {
                    super.onStop()
                    scope.launch { stopCapture() }
                }
            }
            mediaProjection.registerCallback(mediaProjectionCallBack!!, handler)

            val dimensions = screenMetricsDataSource.getScreenDimensions()
            imageReader = imageReaderDataSource.createImageReader(
                width = dimensions.width,
                height = dimensions.height
            )
            virtualDisplay = virtualDisplayDataSource.createVirtualDisplay(
                mediaProjection = mediaProjection,
                width = dimensions.width,
                height = dimensions.height,
                densityDpi = dimensions.densityDpi,
                surface = imageReader!!.surface,
                handler = handler!!
            )

            captureJob = scope.launch {
                while (isActive && _captureState.value is CaptureState.Capturing) {
                    try {
                        imageReader?.let {
                            imageReaderDataSource.readFrame(it)?.let { bitmap ->
                                val frame = CaptureFrame(bitmap = bitmap)
                                lastFrameRef.set(frame)
                                _frame.tryEmit(frame)
                            }
                        }
                    } catch (error: Exception) {
                        break
                    }
                    delay(16)
                }
            }
        } catch (error: Exception) {
            _captureState.value = CaptureState.Error(
                error = error.toString()
            )
        }
    }

    override suspend fun stopCapture() = withContext(Dispatchers.Default) {
        if (_captureState.value == CaptureState.Idle) return@withContext

        try {
            _captureState.value = CaptureState.Idle

            captureJob?.cancelAndJoin()
            captureJob = null

            mediaProjectionCallBack?.let { callback ->
                mediaProjection?.unregisterCallback(callback)
            }

            virtualDisplay?.release()
            virtualDisplay = null

            imageReader?.close()
            imageReader = null

            handlerThread?.quitSafely()
            handlerThread = null
            handler = null

            mediaProjection?.stop()
            mediaProjection = null
            mediaProjectionCallBack = null

            lastFrameRef.set(null)
            _frame.tryEmit(null)
        } catch (error: Exception) {
            _captureState.value = CaptureState.Error(error.toString())
        }
    }

    override fun observeFrame(): Flow<CaptureFrame?> = _frame.asSharedFlow()

    override fun getLastFrame(): CaptureFrame? {
        return lastFrameRef.get()?.let { frame ->
            frame.copy(
                bitmap = frame.bitmap.copy(Bitmap.Config.ARGB_8888, false)
            )
        }
    }
}