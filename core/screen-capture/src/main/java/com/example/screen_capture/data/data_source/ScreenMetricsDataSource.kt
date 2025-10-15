package com.example.screen_capture.data.data_source

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import com.example.screen_capture.data.model.ScreenDimensions
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface ScreenMetricsDataSource {
    fun getScreenDimensions(): ScreenDimensions
}

class ScreenMetricsDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ScreenMetricsDataSource {
    override fun getScreenDimensions(): ScreenDimensions {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getRealMetrics(metrics)

        return ScreenDimensions(
            width = metrics.widthPixels,
            height = metrics.heightPixels,
            densityDpi = metrics.densityDpi
        )
    }
}