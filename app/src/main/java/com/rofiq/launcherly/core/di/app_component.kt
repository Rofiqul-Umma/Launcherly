package com.rofiq.launcherly.core.di
import com.rofiq.launcherly.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, VIewModelModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
}