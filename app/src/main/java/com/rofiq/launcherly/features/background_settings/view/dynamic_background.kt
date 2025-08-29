package com.rofiq.launcherly.features.background_settings.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
import com.rofiq.launcherly.R
import com.rofiq.launcherly.common.color.TVColors
import com.rofiq.launcherly.features.background_settings.model.BackgroundType
import com.rofiq.launcherly.features.background_settings.view_model.BackgroundSettingsViewModel

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun DynamicBackground(
    backgroundVM: BackgroundSettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentBackground = backgroundVM.getCurrentBackground()
    
    when (currentBackground.type) {
        BackgroundType.VIDEO -> {
            val exoPlayer = remember(currentBackground.resourcePath) {
                ExoPlayer.Builder(context).build().apply {
                    val mediaItem = MediaItem.fromUri(currentBackground.resourcePath)
                    setMediaItem(mediaItem)
                    prepare()
                    playWhenReady = true
                    repeatMode = ExoPlayer.REPEAT_MODE_ONE
                    // Add memory optimization
                    videoScalingMode = androidx.media3.common.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                }
            }
            
            DisposableEffect(currentBackground.resourcePath) {
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
                        // Ignore cleanup errors
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
            Image(
                painter = painterResource(id = R.drawable.background_auth),
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}