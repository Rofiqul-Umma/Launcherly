package com.rofiq.launcherly.features.background_settings.view

import android.net.Uri
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
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
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
import androidx.compose.ui.draw.scale
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.rofiq.launcherly.common.color.TVColors
import com.rofiq.launcherly.common.text_style.TVTypography
import com.rofiq.launcherly.features.background_settings.model.BackgroundType
import com.rofiq.launcherly.features.background_settings.view_model.BackgroundSettingsViewModel
import java.io.File

@Composable
fun TVFilePicker(
    navController: NavController,
    backgroundVM: BackgroundSettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var currentPath by remember { mutableStateOf( "/storage/emulated/0/Download") }
    var imageFiles by remember { mutableStateOf<List<File>>(emptyList()) }
    var folderFiles by remember { mutableStateOf<List<File>>(emptyList()) }
    
    // Load files when path changes
    LaunchedEffect(currentPath) {
        loadFiles(currentPath) { folders, images ->
            folderFiles = folders
            imageFiles = images
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .focusGroup()
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            
            Text(
                text = "Select Image",
                style = TVTypography.HeaderLarge.copy(
                    color = TVColors.OnSurface,
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // File grid
        TVFileGrid(
            folderFiles = folderFiles,
            imageFiles = imageFiles,
            onFolderSelected = { folder ->
                currentPath = folder.absolutePath
            },
            onImageSelected = { imageFile ->
                // Convert file to URI
                val uri = Uri.fromFile(imageFile)
                // Handle the selected image
                backgroundVM.setLocalBackground(context, uri, BackgroundType.IMAGE) { success ->
                    if (success) {
                        // Navigate back to home or show success message
                        navController.navigate("home") {
                            popUpTo("background_settings") { inclusive = true }
                        }
                    }
                }
            },
            onNavigateUp = {
                val parent = File(currentPath).parentFile
                if (parent != null && parent.exists()) {
                    currentPath = parent.absolutePath
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TVFileGrid(
    folderFiles: List<File>,
    imageFiles: List<File>,
    onFolderSelected: (File) -> Unit,
    onImageSelected: (File) -> Unit,
    onNavigateUp: () -> Unit
) {
    val allFiles = folderFiles + imageFiles
    var focusedIndex by remember { mutableIntStateOf(0) }
    val focusRequesters = remember(allFiles.size) { 
        List(allFiles.size) { FocusRequester() } 
    }

    LaunchedEffect(Unit) {
        focusRequesters.firstOrNull()?.requestFocus()
    }
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier
            .fillMaxSize()
            .onKeyEvent { keyEvent ->
                if (keyEvent.key == Key.Back && keyEvent.type == KeyEventType.KeyUp) {
                    onNavigateUp()
                    true
                } else {
                    false
                }
            },
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(allFiles) { index, file ->
            val isFocused = focusedIndex == index
            
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
                                    if (file.isDirectory) {
                                        onFolderSelected(file)
                                    } else {
                                        onImageSelected(file)
                                    }
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
                            if (file.isDirectory) {
                                onFolderSelected(file)
                            } else {
                                onImageSelected(file)
                            }
                        }
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.Image,
                        contentDescription = null,
                        tint = if (isFocused) TVColors.OnSurface else TVColors.OnSurface,
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = file.name,
                        style = TVTypography.BodySmall.copy(
                            color = if (isFocused) TVColors.OnSurface else TVColors.OnSurface
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
                
                // Thumbnail for images
                if (!file.isDirectory) {
                    AsyncImage(
                        model = file,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
    
    LaunchedEffect(Unit) {
        // Request focus on the first item if available
        if (focusRequesters.isNotEmpty()) {
            focusRequesters.first().requestFocus()
        }
    }
}

private fun loadFiles(
    path: String,
    onFilesLoaded: (List<File>, List<File>) -> Unit
) {
    val directory = File(path)
    if (!directory.exists() || !directory.isDirectory) {
        onFilesLoaded(emptyList(), emptyList())
        return
    }
    
    val files = directory.listFiles()?.toList() ?: emptyList()
    val folders = files.filter { it.isDirectory }.sortedBy { it.name }
    val images = files.filter { 
        it.isFile && isImageFile(it.name) 
    }.sortedBy { it.name }
    
    onFilesLoaded(folders, images)
}

private fun isImageFile(name: String): Boolean {
    val imageExtensions = listOf(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp")
    return imageExtensions.any { name.lowercase().endsWith(it) }
}