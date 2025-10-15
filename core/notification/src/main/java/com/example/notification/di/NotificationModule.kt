package com.example.notification.di

import android.content.Context
import com.example.notification.data.builder.NotificationBuilder
import com.example.notification.data.builder.NotificationBuilderImpl
import com.example.notification.data.manager.NotificationChannelManager
import com.example.notification.data.manager.NotificationChannelManagerImpl
import com.example.notification.data.repository.NotificationRepositoryImpl
import com.example.notification.domain.repository.NotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {
    @Singleton
    @Provides
    fun provideNotificationBuilder(
        @ApplicationContext context: Context
    ): NotificationBuilder = NotificationBuilderImpl(context)

    @Singleton
    @Provides
    fun provideNotificationChannelManager(
        @ApplicationContext context: Context
    ): NotificationChannelManager = NotificationChannelManagerImpl(context)

    @Singleton
    @Provides
    fun provideNotificationRepository(
        @ApplicationContext context: Context,
        notificationChannelManager: NotificationChannelManager,
        notificationBuilder: NotificationBuilder
    ): NotificationRepository = NotificationRepositoryImpl(
        context = context,
        notificationChannelManager = notificationChannelManager,
        notificationBuilder = notificationBuilder
    )
}