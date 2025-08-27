package com.rofiq.launcherly.core.di

import android.content.Context
import com.rofiq.launcherly.features.device_manager.service.DeviceManagerService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides
    @ActivityContext
    fun provideActivityContext(@ActivityContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideDeviceManagerService(@ActivityContext context: Context): DeviceManagerService{
        return DeviceManagerService(context)
    }
}
