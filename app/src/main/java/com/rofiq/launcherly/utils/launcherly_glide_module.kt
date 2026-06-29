package com.rofiq.launcherly.utils

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

// Tunes Glide for low-RAM Android TV boxes, which is where the background grid was
// being killed. Two levers:
//  - PREFER_RGB_565 halves the bytes-per-pixel of decoded bitmaps vs ARGB_8888.
//    Wallpaper previews don't need an alpha channel, so this is free quality-wise.
//  - A memory/bitmap-pool size derived from MemorySizeCalculator at a reduced
//    fraction keeps the in-memory cache from overshooting and OOM-ing when the
//    grid decodes ~20 previews at once.
@GlideModule
class LauncherlyGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val calculator = MemorySizeCalculator.Builder(context)
            .setMemoryCacheScreens(1.5f)
            .setBitmapPoolScreens(1.5f)
            .build()
        builder.setMemoryCache(LruResourceCache(calculator.memoryCacheSize.toLong()))
        builder.setBitmapPool(LruBitmapPool(calculator.bitmapPoolSize.toLong()))
        builder.setDefaultRequestOptions(
            RequestOptions().format(DecodeFormat.PREFER_RGB_565)
        )
    }

    // We don't ship manifest-declared GlideModules, so skip the legacy parse pass.
    override fun isManifestParsingEnabled(): Boolean = false
}
