package com.rofiq.launcherly.utils

import android.content.Context
import coil.ImageLoader
import coil.memory.MemoryCache
import coil.util.DebugLogger

object CoilImageLoader {
    fun createImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25) // Use 25% of the application's available memory
                    .build()
            }
            .diskCache {
                coil.disk.DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50 * 1024 * 1024) // 50 MB disk cache
                    .build()
            }
            .logger( DebugLogger() ) // Enable logging in debug mode
            .build()
    }
}