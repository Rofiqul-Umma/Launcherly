package com.rofiq.launcherly.core.di

import com.rofiq.launcherly.features.check_internet.service.CheckInternetService
import com.rofiq.launcherly.features.check_internet.view_model.CheckInternetViewModel
import com.rofiq.launcherly.features.fetch_date_time.service.FetchDateTimeService
import com.rofiq.launcherly.features.fetch_date_time.view_model.FetchDateTimeViewModel
import com.rofiq.launcherly.features.get_account_info.service.GetAccountInfoService
import com.rofiq.launcherly.features.get_account_info.view_model.GetAccountInfoViewModel
import com.rofiq.launcherly.features.home.service.HomeService
import com.rofiq.launcherly.features.home.view_model.HomeViewModel
import com.rofiq.launcherly.features.launch_app.service.LaunchAppService
import com.rofiq.launcherly.features.launch_app.view_model.LaunchAppViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VIewModelModule {

    @Provides
    @Singleton
    fun provideHomeViewModel(homeService: HomeService): HomeViewModel {
        return HomeViewModel(homeService)
    }

    @Provides
    @Singleton
    fun provideFetchDateTimeViewModel(fetchDateTimeService: FetchDateTimeService): FetchDateTimeViewModel {
        return FetchDateTimeViewModel(fetchDateTimeService)
    }

    @Provides
    @Singleton
    fun provideGetAccountInfoViewModel(getAccountInfoService: GetAccountInfoService): GetAccountInfoViewModel {
        return GetAccountInfoViewModel(getAccountInfoService)
    }

    @Provides
    @Singleton
    fun provideCheckInternetViewModel(checkInternetService: CheckInternetService): CheckInternetViewModel {
        return CheckInternetViewModel(checkInternetService)
    }

    @Provides
    @Singleton
    fun provideLaunchViewModel(launchService: LaunchAppService): LaunchAppViewModel {
        return LaunchAppViewModel(launchService)
    }
}