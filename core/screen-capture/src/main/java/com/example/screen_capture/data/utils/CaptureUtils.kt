package com.example.screen_capture.data.utils

import android.graphics.Bitmap
import android.media.Image

object CaptureUtils {
    fun imageToBitmap(image: Image): Bitmap? {
            val plane = image.planes[0]
            val buffer = plane.buffer
            val pixelStride = plane.pixelStride
            val rowStride = plane.rowStride
            val width = image.width
            val height = image.height

            if (width <= 0 || height <= 0 || pixelStride <= 0 || rowStride <= 0) return null

            val rowPadding = (rowStride - pixelStride * width).coerceAtLeast(0)
            val paddedWidth = width + rowPadding / pixelStride
            val bitmap = Bitmap.createBitmap(paddedWidth, height, Bitmap.Config.ARGB_8888)
            bitmap.copyPixelsFromBuffer(buffer)
                Bitmap.createBitmap(bitmap, 0, 0, width, height)
            }
        }
    }