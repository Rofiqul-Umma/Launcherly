package com.rofiq.launcherly.features.background_settings.view

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
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
import com.rofiq.launcherly.features.background_settings.view_model.BackgroundSettingsLoaded
import com.rofiq.launcherly.features.background_settings.view_model.BackgroundSettingsViewModel
import com.rofiq.launcherly.utils.GoogleDriveUtils

@OptIn(UnstableApi::class)
@Composable
fun DynamicBackground(
    backgroundVM: BackgroundSettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current.applicationContext
    val lifecycleOwner = LocalLifecycleOwner.current
    val backgroundSettingsState by backgroundVM.backgroundSettingsState.collectAsState()

    // Observe the ExoPlayer instance from the ViewModel
    val exoPlayer by backgroundVM.exoPlayer.collectAsState()

    // Determine the current background to display
    val currentBackground = when (backgroundSettingsState) {
        is BackgroundSettingsLoaded -> (backgroundSettingsState as BackgroundSettingsLoaded).currentBackground
        else -> null
    }

    LaunchedEffect(currentBackground, context) {
        currentBackground?.let { background ->
            if (background.type == BackgroundType.VIDEO) {
                val videoUrl = if (background.sourceType == com.rofiq.launcherly.features.background_settings.model.BackgroundSourceType.URL) {
                    GoogleDriveUtils.convertSharingUrlToDownloadUrl(background.resourcePath)
                } else {
                    background.resourcePath
                }
                if (videoUrl.isNotBlank()) {
                    backgroundVM.initializePlayer(context, videoUrl)
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner, backgroundVM, currentBackground) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    currentBackground?.let { background ->
                        if (background.type == BackgroundType.VIDEO) {
                            // Ensure player is initialized and playing when resuming
                            val videoUrl = if (background.sourceType == com.rofiq.launcherly.features.background_settings.model.BackgroundSourceType.URL) {
                                GoogleDriveUtils.convertSharingUrlToDownloadUrl(background.resourcePath)
                            } else {
                                background.resourcePath
                            }
                            if (videoUrl.isNotBlank()) {
                                backgroundVM.initializePlayer(context, videoUrl)
                                backgroundVM.playPlayer()
                            }
                        }
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    // Don't pause the player immediately to maintain the last frame
                    // backgroundVM.pausePlayer()
                }
                Lifecycle.Event.ON_STOP -> {
                    // Only pause when the app is fully stopped
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
        }
    }

    currentBackground?.let { background ->
        when (background.type) {
            BackgroundType.VIDEO -> {
                // Always show the player view, even if player is null (to avoid black screen)
                Box(modifier = Modifier.fillMaxSize()) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                exoPlayer?.let { player ->
                                    this.player = player
                                }
                                useController = false
                                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            }
                        },
                        update = { playerView ->
                            exoPlayer?.let { player ->
                                if (playerView.player != player) {
                                    playerView.player = player
                                }
                                // Ensure player state is correct based on lifecycle
                                val shouldPlay = lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
                                if (player.playWhenReady != shouldPlay) {
                                     player.playWhenReady = shouldPlay
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            BackgroundType.IMAGE -> {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(background.directUrl)
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
    } ?: run {
        // Fallback to default background if currentBackground is null
        Image(
            painter = painterResource(id = R.drawable.background_auth),
            contentDescription = "Default Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
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
