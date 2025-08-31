package com.rofiq.launcherly.features.background_settings.view

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.foundation.Image // Import Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Scale
import com.rofiq.launcherly.R
import com.rofiq.launcherly.features.background_settings.model.BackgroundType
import com.rofiq.launcherly.features.background_settings.view_model.BackgroundSettingsViewModel
import com.rofiq.launcherly.features.background_settings.view_model.BackgroundSettingsLoaded // Ensure this state is imported
import com.rofiq.launcherly.utils.GoogleDriveUtils

@OptIn(UnstableApi::class)
@Composable
fun DynamicBackground(
    backgroundVM: BackgroundSettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current.applicationContext // Use application context
    val lifecycleOwner = LocalLifecycleOwner.current
    val backgroundSettingsState by backgroundVM.backgroundSettingsState.collectAsState() // Observe state

    // Observe the ExoPlayer instance from the ViewModel
    val exoPlayer by backgroundVM.exoPlayer.collectAsState()

    // Determine the current background to display
    val currentBackground = (backgroundSettingsState as BackgroundSettingsLoaded).currentBackground


    LaunchedEffect(currentBackground, context) {
        if (currentBackground.type == BackgroundType.VIDEO) {
            val videoUrl = if (currentBackground.sourceType == com.rofiq.launcherly.features.background_settings.model.BackgroundSourceType.URL) {
                GoogleDriveUtils.convertSharingUrlToDownloadUrl(currentBackground.resourcePath)
            } else {
                currentBackground.resourcePath
            }
            if (videoUrl.isNotBlank()) {
                backgroundVM.initializePlayer(context, videoUrl)
            }
        } else {
            backgroundVM.releasePlayer()
        }
    }

    DisposableEffect(lifecycleOwner, backgroundVM, currentBackground) { // Added currentBackground as a key
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (currentBackground.type == BackgroundType.VIDEO) {
                        backgroundVM.playPlayer()
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    backgroundVM.pausePlayer()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    backgroundVM.pausePlayer()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            backgroundVM.pausePlayer() // Pause when composable is disposed
        }
    }

    when (currentBackground.type) {
        BackgroundType.VIDEO -> {
            exoPlayer?.let { player ->
                if (!player.isReleased) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AndroidView(
                            factory = { ctx ->
                                PlayerView(ctx).apply {
                                    this.player = player
                                    useController = false
                                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                }
                            },
                            update = { playerView ->
                                if (playerView.player != player) {
                                    playerView.player = player
                                }
                                // Ensure player state is correct based on lifecycle
                                val shouldPlay = lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
                                if (player.playWhenReady != shouldPlay) {
                                     player.playWhenReady = shouldPlay
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    ShowImagePlaceholder(context, "Video player was released, showing placeholder.")
                }
            } ?: run {
                ShowImagePlaceholder(context, "Video player not available, showing placeholder.")
            }
        }

        BackgroundType.IMAGE -> {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(currentBackground.directUrl)
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .scale(Scale.FIT)
                    .build(),
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.background_auth),
                error = painterResource(id = R.drawable.background_auth)
            )
        }
    }
}

@Composable
private fun ShowImagePlaceholder(context: Context, reason: String) {
    // Use androidx.compose.foundation.Image for painters
    Image(
        painter = painterResource(id = R.drawable.background_auth),
        contentDescription = "Placeholder Background",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}
