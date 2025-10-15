package com.example.screen_capture.presentation.screens

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.screen_capture.domain.model.CaptureState
import com.example.screen_capture.service.CaptureService
import com.example.screen_capture.service.CaptureService.Companion.EXTRA_DATA
import com.example.screen_capture.service.CaptureService.Companion.EXTRA_RESULT_CODE

@Composable
fun CaptureScreen(
    viewModel: CaptureViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val captureState by viewModel.captureState.collectAsState()
    val screenFrame by viewModel.screenFrame.collectAsState()
    val mediaProjectionIntent by viewModel.mediaProjectionIntent.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val intent = Intent(context, CaptureService::class.java).apply {
                action = CaptureService.ACTION_START
                putExtra(EXTRA_RESULT_CODE, result.resultCode)
                putExtra(EXTRA_DATA, result.data)
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            viewModel.clearMediaProjectionIntent()
        }
    }

    LaunchedEffect(mediaProjectionIntent) {
        mediaProjectionIntent?.let { launcher.launch(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(9f / 16f)
                .weight(1f, fill = false),
            contentAlignment = Alignment.Center
        ) {
            when (captureState) {
                is CaptureState.Capturing -> {
                    screenFrame?.let { bitmap ->
                        Image(
                            bitmap = bitmap.bitmap.asImageBitmap(),
                            contentDescription = "Screen capture preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } ?: run {
                        CircularProgressIndicator()
                    }
                }
                is CaptureState.Idle -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No capture running",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                is CaptureState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Capture failed",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        Button(
            onClick = { viewModel.requestMediaProjection() },
            enabled = captureState is CaptureState.Idle
        ) { Text(text = "StartCapture") }

        Button(
            onClick = {
                val intent = Intent(context, CaptureService::class.java).apply {
                    action = CaptureService.ACTION_STOP
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            },
            enabled = captureState is CaptureState.Capturing
        ) { Text(text = "StopCapture") }
    }
}