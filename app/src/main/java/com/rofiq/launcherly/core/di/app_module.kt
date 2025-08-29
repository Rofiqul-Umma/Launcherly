package com.rofiq.launcherly.core.di

import android.content.Context
import com.rofiq.launcherly.core.shared_prefs_helper.SharedPrefsHelper
import com.rofiq.launcherly.features.auth.service.AuthService
import com.rofiq.launcherly.features.background_settings.service.BackgroundSettingsService
import com.rofiq.launcherly.features.check_internet.service.CheckInternetService
import com.rofiq.launcherly.features.device_manager.service.DeviceManagerService
import com.rofiq.launcherly.features.fetch_date_time.service.FetchDateTimeService
import com.rofiq.launcherly.features.home.service.HomeService
import com.rofiq.launcherly.features.launch_app.service.LaunchAppService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideSharedPrefsHelper(@ApplicationContext context: Context ): SharedPrefsHelper {
        return SharedPrefsHelper(context)
    }

    @Provides
    @Singleton
    fun provideHomeService(@ApplicationContext context: Context): HomeService {
        return HomeService(context)
    }

    @Provides
    @Singleton
    fun provideFetchDateTimeService(): FetchDateTimeService {
        return FetchDateTimeService()
    }

    @Provides
    @Singleton
    fun provideCheckInternetService(@ApplicationContext context: Context): CheckInternetService {
        return CheckInternetService(context)
    }

    @Provides
    @Singleton
    fun provideAuthService(sharedPrefs: SharedPrefsHelper): AuthService {
        return AuthService(sharedPrefs)
    }

    @Provides
    @Singleton
    fun provideDeviceManagerService(@ApplicationContext context: Context): DeviceManagerService{
        return DeviceManagerService(context)
    }

    @Provides
    @Singleton
    fun provideLaunchAppService(@ApplicationContext context: Context): LaunchAppService {
        return LaunchAppService(context)
    }

    @Provides
    @Singleton
    fun provideBackgroundSettingsService(sharedPrefs: SharedPrefsHelper): BackgroundSettingsService {
        return BackgroundSettingsService(sharedPrefs)
    }
}
