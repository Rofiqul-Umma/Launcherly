package com.rofiq.launcherly.core.di

import android.content.Context
import com.rofiq.launcherly.features.check_internet.service.CheckInternetService
import com.rofiq.launcherly.features.fetch_date_time.service.FetchDateTimeService
import com.rofiq.launcherly.features.get_account_info.service.GetAccountInfoService
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
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
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
    fun provideGetAccountInfoService(@ApplicationContext context: Context): GetAccountInfoService {
        return GetAccountInfoService(context)
    }

    @Provides
    @Singleton
    fun provideCheckInternetService(@ApplicationContext context: Context): CheckInternetService {
        return CheckInternetService(context)
    }

    @Provides
    @Singleton
    fun provideLaunchAppService(@ApplicationContext context: Context): LaunchAppService {
        return LaunchAppService(context)
    }
}
