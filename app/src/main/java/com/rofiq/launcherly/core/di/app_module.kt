package com.rofiq.launcherly.core.di

import android.content.Context
import coil.ImageLoader
import coil.decode.VideoFrameDecoder
import com.rofiq.launcherly.core.shared_prefs_helper.SharedPrefsHelper
import com.rofiq.launcherly.features.auth.service.AuthService
import com.rofiq.launcherly.features.background_settings.service.BackgroundSettingsService
import com.rofiq.launcherly.features.check_internet.service.CheckInternetService
import com.rofiq.launcherly.features.device_manager.service.DeviceManagerService
import com.rofiq.launcherly.features.favorite_apps.service.FavoriteAppsService
import com.rofiq.launcherly.features.fetch_date_time.service.FetchDateTimeService
import com.rofiq.launcherly.features.generate_video_thumbnails.service.GenerateVideoThumbnailsService
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
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideSharedPrefsHelper(@ApplicationContext context: Context ): SharedPrefsHelper {
        return SharedPrefsHelper(context)
    }

    @Provides
    @Singleton
    fun provideHomeService(@ApplicationContext context: Context, favoriteAppsService: FavoriteAppsService): HomeService {
        return HomeService(context, favoriteAppsService)
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

    @Provides
    @Singleton
    fun provideGenerateVideoThumbnailsService(@ApplicationContext context: Context): GenerateVideoThumbnailsService {
        return GenerateVideoThumbnailsService(context)
    }

    @Provides
    @Singleton
    fun provideFavoriteAppsService(@ApplicationContext context: Context): FavoriteAppsService {
        return FavoriteAppsService(context)
    }
}
