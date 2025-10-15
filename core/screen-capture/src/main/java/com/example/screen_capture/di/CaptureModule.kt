package com.example.screen_capture.di

import android.content.Context
import com.example.screen_capture.data.data_source.ImageReaderDataSource
import com.example.screen_capture.data.data_source.ImageReaderDataSourceImpl
import com.example.screen_capture.data.data_source.ScreenMetricsDataSource
import com.example.screen_capture.data.data_source.ScreenMetricsDataSourceImpl
import com.example.screen_capture.data.data_source.VirtualDisplayDataSource
import com.example.screen_capture.data.data_source.VirtualDisplayDataSourceImpl
import com.example.screen_capture.data.repository.CaptureRepositoryImpl
import com.example.screen_capture.data.repository.MediaProjectionRepositoryImpl
import com.example.screen_capture.domain.repository.CaptureRepository
import com.example.screen_capture.domain.repository.MediaProjectionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CaptureBindModule {
    @Singleton
    @Binds
    abstract fun bindImageReaderDataSource(
        impl: ImageReaderDataSourceImpl
    ): ImageReaderDataSource

    @Singleton
    @Binds
    abstract fun bindVirtualDisplayDataSource(
        impl: VirtualDisplayDataSourceImpl
    ): VirtualDisplayDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object CaptureProvideModule {
    @Singleton
    @Provides
    fun provideScreenMetricsDataSource(
        @ApplicationContext context: Context
    ): ScreenMetricsDataSource = ScreenMetricsDataSourceImpl(context)

    @Singleton
    @Provides
    fun provideMediaProjectionRepository(
        @ApplicationContext context: Context
    ): MediaProjectionRepository = MediaProjectionRepositoryImpl(context)

    @Singleton
    @Provides
    fun provideCaptureRepository(
        screenMetricsDataSource: ScreenMetricsDataSourceImpl,
        imageReaderDataSource: ImageReaderDataSource,
        virtualDisplayDataSource: VirtualDisplayDataSource
    ): CaptureRepository = CaptureRepositoryImpl(
        screenMetricsDataSource = screenMetricsDataSource,
        imageReaderDataSource = imageReaderDataSource,
        virtualDisplayDataSource = virtualDisplayDataSource
    )
}