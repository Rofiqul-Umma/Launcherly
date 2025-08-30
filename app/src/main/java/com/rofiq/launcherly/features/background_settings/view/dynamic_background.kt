package com.rofiq.launcherly.features.background_settings.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.CachePolicy
import com.rofiq.launcherly.R
import com.rofiq.launcherly.common.widgets.LCircularLoading
import com.rofiq.launcherly.features.background_settings.model.BackgroundType
import com.rofiq.launcherly.features.background_settings.view_model.BackgroundSettingsViewModel
import com.rofiq.launcherly.utils.GoogleDriveUtils

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun DynamicBackground(
    backgroundVM: BackgroundSettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentBackground = backgroundVM.getCurrentBackground()
    
    // Remember the last loaded background to prevent flickering
    var lastLoadedBackground by remember { mutableStateOf(currentBackground) }
    
    // Update last loaded background when current background changes
    LaunchedEffect(currentBackground) {
        lastLoadedBackground = currentBackground
    }
    
    when (lastLoadedBackground.type) {
        BackgroundType.VIDEO -> {
            // Convert Google Drive URL to direct download URL for video playback
            val videoUrl = if (lastLoadedBackground.sourceType == com.rofiq.launcherly.features.background_settings.model.BackgroundSourceType.URL) {
                GoogleDriveUtils.convertSharingUrlToDownloadUrl(lastLoadedBackground.resourcePath)
            } else {
                lastLoadedBackground.resourcePath
            }
            
            android.util.Log.d("DynamicBackground", "Playing video from URL: $videoUrl")
            
            val exoPlayer = remember(videoUrl) {
                ExoPlayer.Builder(context).build().apply {
                    val mediaItem = MediaItem.fromUri(videoUrl)
                    setMediaItem(mediaItem)
                    prepare()
                    playWhenReady = true
                    repeatMode = ExoPlayer.REPEAT_MODE_ONE
                    // Add memory optimization
                    videoScalingMode = androidx.media3.common.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                }
            }
            
            DisposableEffect(videoUrl) {
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_RESUME -> {
                            if (!exoPlayer.isReleased) {
                                exoPlayer.playWhenReady = true
                            }
                        }
                        Lifecycle.Event.ON_START -> {
                            if (!exoPlayer.isReleased) {
                                exoPlayer.playWhenReady = true
                            }
                        }
                        Lifecycle.Event.ON_PAUSE -> {
                            if (!exoPlayer.isReleased) {
                                exoPlayer.playWhenReady = false
                            }
                        }
                        Lifecycle.Event.ON_STOP -> {
                            if (!exoPlayer.isReleased) {
                                exoPlayer.stop()
                                exoPlayer.clearMediaItems()
                            }
                        }
                        else -> {}
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    try {
                        if (!exoPlayer.isReleased) {
                            exoPlayer.stop()
                            exoPlayer.clearMediaItems()
                            exoPlayer.release()
                        }
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    } catch (e: Exception) {
                        android.util.Log.e("DynamicBackground", "Error releasing ExoPlayer", e)
                    }
                }
            }
            
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    factory = { context ->
                        PlayerView(context).apply {
                            player = exoPlayer
                            useController = false
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        }
                    },
                    update = { playerView ->
                        if (playerView.player != exoPlayer) {
                            playerView.player = exoPlayer
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        
        BackgroundType.IMAGE -> {
            // Use AsyncImage which handles caching and lifecycle better
            AsyncImage(
                model = coil.request.ImageRequest.Builder(context)
                    .data(lastLoadedBackground.directUrl)
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                // Show the last loaded image while loading new one
                placeholder = painterResource(id = R.drawable.background_auth),
                error = painterResource(id = R.drawable.background_auth)
            )
        }
    }
}