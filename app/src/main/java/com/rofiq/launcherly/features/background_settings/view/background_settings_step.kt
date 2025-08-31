package com.rofiq.launcherly.features.background_settings.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import com.rofiq.launcherly.common.color.TVColors
import com.rofiq.launcherly.common.text_style.TVTypography
import com.rofiq.launcherly.common.widgets.LCircularLoading
import com.rofiq.launcherly.features.background_settings.model.BackgroundSetting
import com.rofiq.launcherly.features.background_settings.model.BackgroundType
import com.rofiq.launcherly.features.background_settings.view_model.BackgroundSettingsLoaded
import com.rofiq.launcherly.features.background_settings.view_model.BackgroundSettingsLoading
import com.rofiq.launcherly.features.background_settings.view_model.BackgroundSettingsViewModel
import com.rofiq.launcherly.features.generate_video_thumbnails.view_model.GenerateVideoThumbnailsViewModel
import com.rofiq.launcherly.features.guided_settings.view.GuidedStepLayout
import com.rofiq.launcherly.utils.GoogleDriveUtils

@Composable
fun BackgroundSettingsStep(
    onBack: () -> Unit,
    navController: NavController,
    backgroundVM: BackgroundSettingsViewModel = hiltViewModel()
) {
    val backgroundState by backgroundVM.backgroundSettingsState.collectAsState()
    val context = LocalContext.current

    GuidedStepLayout(
        title = "Background Settings",
        description = "Choose your home screen background",
    ) {
        when (backgroundState) {
            is BackgroundSettingsLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LCircularLoading()
                }
            }

            is BackgroundSettingsLoaded -> {
                BackgroundGrid(
                    backgrounds = (backgroundState as BackgroundSettingsLoaded).availableBackgrounds,
                    currentBackground = (backgroundState as BackgroundSettingsLoaded).currentBackground,
                    onBackgroundSelected = { 
                        backgroundVM.setBackground(it, context)
                        navController.navigate("home") {
                            popUpTo("background_settings") { inclusive = true }
                        }
                    },
                    onBack = onBack
                )
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error loading backgrounds",
                        style = TVTypography.BodyLarge.copy(color = TVColors.OnSurface)
                    )
                }
            }
        }
    }
}

@Composable
fun BackgroundGrid(
    backgrounds: List<BackgroundSetting>,
    currentBackground: BackgroundSetting,
    onBackgroundSelected: (BackgroundSetting) -> Unit,
    onBack: () -> Unit
) {
    var focusedIndex by remember { mutableIntStateOf(0) }
    val focusRequesters = remember { backgrounds.map { FocusRequester() } }


    LaunchedEffect(Unit) {
        focusRequesters.firstOrNull()?.requestFocus()
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
    ) {
        itemsIndexed(backgrounds) { index, background ->
            BackgroundCard(
                background = background,
                isSelected = background.resourcePath == currentBackground.resourcePath,
                isFocused = focusedIndex == index,
                focusRequester = focusRequesters[index],
                onFocusChanged = { focused ->
                    if (focused) focusedIndex = index
                },
                onSelected = { onBackgroundSelected(background) },
                onBack = onBack
            )
        }
    }
}

@Composable
fun BackgroundCard(
    background: BackgroundSetting,
    isSelected: Boolean,
    isFocused: Boolean,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit,
    onSelected: () -> Unit,
    onBack: () -> Unit,
) {
    val generateVideoThumbVM: GenerateVideoThumbnailsViewModel = hiltViewModel(
        key = background.resourcePath
    )

    // Generate thumbnail for this specific video when the card is first composed
    LaunchedEffect(background) {
        generateVideoThumbVM.generateThumbnailForVideo(background.resourcePath)
    }

    val generateThumbState = generateVideoThumbVM.generateVideoThumbState.collectAsState()

    val context = LocalContext.current

    Card(
        modifier = Modifier
            .aspectRatio(16f / 9f)
            .focusRequester(focusRequester)
            .onFocusChanged { onFocusChanged(it.isFocused) }
            .focusable()
            .border(
                width = if (isFocused) 3.dp else 0.dp,
                color = if (isFocused) TVColors.OnSurface else androidx.compose.ui.graphics.Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .onKeyEvent { keyEvent ->
                if ((keyEvent.key == Key.DirectionCenter || keyEvent.key == Key.Enter) && keyEvent.type == KeyEventType.KeyUp) {
                    onSelected()
                    true
                } else false
            },
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> TVColors.OnSurfaceSecondary.copy(alpha = 0.2f)
                else -> TVColors.Surface.copy(alpha = 0.6f)
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background preview
            when (background.type) {
                BackgroundType.IMAGE -> {
                    // Use AsyncImage for better caching and lifecycle handling
                    AsyncImage(
                        model = coil.request.ImageRequest.Builder(context)
                            .data(background.directUrl)
                            .crossfade(true)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = background.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                BackgroundType.VIDEO -> {
                    // Video thumbnail placeholder
                    Box(
                        modifier = Modifier // ktlint-disable no-empty-glam-lambda
                            .fillMaxSize()
                            .background(TVColors.Surface),
                        contentAlignment = Alignment.Center
                    ) {
                        val thumbnail = generateThumbState.value
                        if (thumbnail != null) {
                            Image(
                                bitmap = thumbnail.asImageBitmap(), // ktlint-disable indentation
                                contentDescription = background.name,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // Try to load Google Drive thumbnail using Coil or use directUrl if available
                            val thumbnailUrl =
                                if (background.resourcePath.contains("drive.google.com")) {
                                    GoogleDriveUtils.createThumbnailUrl(background.resourcePath)
                                } else {
                                    null
                                }

                            if (thumbnailUrl != null) {
                                AsyncImage(
                                    model = coil.request.ImageRequest.Builder(context)
                                        .data(thumbnailUrl)
                                        .crossfade(true)
                                        .memoryCachePolicy(CachePolicy.ENABLED)
                                        .diskCachePolicy(CachePolicy.ENABLED)
                                        .build(),
                                    contentDescription = background.name,
                                    modifier = Modifier // ktlint-disable no-empty-glam-lambda
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop,
                                    // Fallback to video icon if thumbnail fails
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.VideoLibrary,
                                    contentDescription = "Video",
                                    tint = TVColors.OnSurface,
                                    modifier = Modifier.size(32.dp) // ktlint-disable no-empty-glam-lambda
                                )
                            }
                        }
                    }
                }
            }

            // Selection indicator
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(TVColors.OnSurfaceSecondary.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = TVColors.OnSurface,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Background name
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        TVColors.Surface.copy(alpha = 0.8f),
                        RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = background.name,
                    style = TVTypography.BodySmall.copy(color = TVColors.OnSurface),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}