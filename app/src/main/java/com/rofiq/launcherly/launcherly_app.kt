package com.rofiq.launcherly

import android.app.Application
import coil.Coil
import com.rofiq.launcherly.utils.CoilImageLoader
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LauncherlyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Coil with custom configuration
        val imageLoader = CoilImageLoader.createImageLoader(this)
        Coil.setImageLoader(imageLoader)
    }
}
