package com.example.screen_capture.data.data_source

import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.media.Image
import android.media.ImageReader
import com.example.screen_capture.data.utils.CaptureUtils.imageToBitmap
import javax.inject.Inject

interface ImageReaderDataSource {
    fun createImageReader(
        width: Int,
        height: Int
    ): ImageReader
    fun readFrame(imageReader: ImageReader): Bitmap?
}

class ImageReaderDataSourceImpl @Inject constructor() : ImageReaderDataSource {
    override fun createImageReader(
        width: Int,
        height: Int
    ): ImageReader {
        return ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)
    }

    override fun readFrame(imageReader: ImageReader): Bitmap? {
        return imageReader.acquireLatestImage()?.use { image: Image ->
            imageToBitmap(image)
        }
    }
}