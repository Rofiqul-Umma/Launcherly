package com.rofiq.launcherly.features.background_settings.view

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import com.rofiq.launcherly.common.color.TVColors
import com.rofiq.launcherly.common.text_style.TVTypography
import com.rofiq.launcherly.common.widgets.LCircularLoading
import com.rofiq.launcherly.features.background_settings.model.BackgroundType
import com.rofiq.launcherly.features.background_settings.view_model.BackgroundSettingsViewModel

data class MediaItem(val uri: Uri, val mediaType: Int)

@Composable
fun TVFilePicker(
    navController: NavController,
    backgroundVM: BackgroundSettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var mediaFiles by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
    var permissionsGranted by remember { mutableStateOf(false) }

    val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
        )
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            permissionsGranted = permissions.values.all { it }
        }
    )

    LaunchedEffect(Unit) {
        launcher.launch(permissionsToRequest)
    }

    LaunchedEffect(permissionsGranted) {
        if (permissionsGranted) {
            mediaFiles = backgroundVM.loadMediaFromMediaStore(context).map { MediaItem(it.uri, it.mediaType) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .focusGroup()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Select Media",
                style = TVTypography.HeaderLarge.copy(
                    color = TVColors.OnSurface,
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (permissionsGranted) {
            TVFileGrid(
                mediaFiles = mediaFiles,
                onMediaSelected = { mediaItem ->
                    val backgroundType = if (mediaItem.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                        BackgroundType.IMAGE
                    } else {
                        BackgroundType.VIDEO
                    }
                    backgroundVM.setLocalBackground(context, mediaItem.uri, backgroundType) { success ->
                        if (success) {
                            navController.navigate("home") {
                                popUpTo("background_settings") { inclusive = true }
                            }
                        }
                    }
                },
                imageLoader = backgroundVM.imageLoader
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Storage permission is required to select a background. Please grant the permission.",
                    style = TVTypography.BodyLarge,
                    color = TVColors.OnSurface,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TVFileGrid(
    mediaFiles: List<MediaItem>,
    onMediaSelected: (MediaItem) -> Unit,
    imageLoader: ImageLoader
) {
    var focusedIndex by remember { mutableIntStateOf(0) }
    val focusRequesters = remember(mediaFiles.size) {
        List(mediaFiles.size) { FocusRequester() }
    }

    LaunchedEffect(Unit) {
        focusRequesters.firstOrNull()?.requestFocus()
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(mediaFiles) { index, mediaItem ->
            val isFocused = focusedIndex == index

            Log.e("COMPOSE", "MediaItem: $mediaItem. uri: ${mediaItem.uri.path}")

            Box(
                modifier = Modifier
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isFocused) TVColors.OnSurface.copy(alpha = 0.3f)
                        else TVColors.Surface.copy(alpha = 0.8f)
                    )
                    .border(
                        width = 3.dp,
                        color = if (isFocused) TVColors.OnSurface else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .focusRequester(focusRequesters[index])
                    .onFocusChanged {
                        if (it.isFocused) focusedIndex = index
                    }
                    .focusable()
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.type == KeyEventType.KeyUp) {
                            when (keyEvent.key) {
                                Key.DirectionCenter, Key.Enter -> {
                                    onMediaSelected(mediaItem)
                                    true
                                }

                                else -> false
                            }
                        } else {
                            false
                        }
                    }
                    .combinedClickable(
                        onClick = {
                            onMediaSelected(mediaItem)
                        }
                    )
            ) {
                SubcomposeAsyncImage(
                    model = mediaItem.uri,
                    imageLoader = imageLoader,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LCircularLoading(
                                size = 30,
                                strokeWidth = 4
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideoLibrary,
                                contentDescription = "Video",
                                tint = TVColors.OnSurface,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        if (focusRequesters.isNotEmpty()) {
            focusRequesters.first().requestFocus()
        }
    }
}