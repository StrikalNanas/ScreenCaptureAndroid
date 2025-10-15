package com.example.screen_capture.presentation.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.screen_capture.domain.model.CaptureState

@Composable
fun CaptureScreen(
    viewModel: CaptureViewModel = hiltViewModel()
) {
    val captureState by viewModel.captureState.collectAsState()
    val screenFrame by viewModel.screenFrame.collectAsState()
    val mediaProjectionIntent by viewModel.mediaProjectionIntent.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
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
            enabled = captureState is CaptureState.Capturing
        ) { Text(text = "StopCapture") }
    }
}