package com.rofiq.launcherly.features.background_settings.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rofiq.launcherly.common.color.TVColors
import com.rofiq.launcherly.common.text_style.TVTypography
import com.rofiq.launcherly.features.background_settings.model.BackgroundType
import com.rofiq.launcherly.features.background_settings.view_model.BackgroundSettingsViewModel

@Composable
fun LocalFilePicker(
    navController: NavController,
    backgroundVM: BackgroundSettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    // Focus requesters for navigation
    val imageCardFocusRequester = remember { FocusRequester() }
    val videoCardFocusRequester = remember { FocusRequester() }
    val backButtonFocusRequester = remember { FocusRequester() }
    
    var focusedElement by remember { mutableIntStateOf(0) } // 0 = image, 1 = video, 2 = back

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                // Handle the selected image
                backgroundVM.setLocalBackground(context, it, BackgroundType.IMAGE) { success ->
                    if (success) {
                        // Navigate back to home or show success message
                        navController.navigate("home") {
                            popUpTo("background_settings") { inclusive = true }
                        }
                    }
                }
            }
        }
    )

    // Video picker launcher
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                // Handle the selected video
                backgroundVM.setLocalBackground(context, it, BackgroundType.VIDEO) { success ->
                    if (success) {
                        // Navigate back to home or show success message
                        navController.navigate("home") {
                            popUpTo("background_settings") { inclusive = true }
                        }
                    }
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Choose Local Background",
            style = TVTypography.HeaderLarge.copy(
                color = TVColors.OnSurface,
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Select from your device storage",
            style = TVTypography.BodyLarge.copy(color = TVColors.OnSurfaceVariant),
            modifier = Modifier.padding(bottom = 40.dp)
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Image Picker Card
            LocalFileCard(
                icon = Icons.Default.Image,
                title = "Image",
                description = "Choose a photo from your gallery",
                isFocused = focusedElement == 0,
                focusRequester = imageCardFocusRequester,
                onFocusChanged = { if (it) focusedElement = 0 },
                onSelected = {
                    imagePickerLauncher.launch("image/*")
                }
            )

            Spacer(modifier = Modifier.width(32.dp))

            // Video Picker Card
            LocalFileCard(
                icon = Icons.Default.VideoLibrary,
                title = "Video",
                description = "Choose a video from your gallery",
                isFocused = focusedElement == 1,
                focusRequester = videoCardFocusRequester,
                onFocusChanged = { if (it) focusedElement = 1 },
                onSelected = {
                    videoPickerLauncher.launch("video/*")
                }
            )
        }
    }
}

@Composable
fun LocalFileCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isFocused: Boolean,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit,
    onSelected: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(TVColors.Surface.copy(alpha = 0.8f))
            .border(
                width = if (isFocused) 3.dp else 0.dp,
                color = if (isFocused) TVColors.OnSurface else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .focusRequester(focusRequester)
            .onFocusChanged { onFocusChanged(it.isFocused) }
            .focusable()
            .onKeyEvent { keyEvent ->
                if ((keyEvent.key == Key.DirectionCenter || keyEvent.key == Key.Enter) && keyEvent.type == KeyEventType.KeyUp) {
                    onSelected()
                    true
                } else false
            }
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Transparent)
                .padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = TVColors.OnSurface,
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = TVTypography.BodyLarge.copy(
                color = TVColors.OnSurface,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            style = TVTypography.BodyRegular.copy(color = TVColors.OnSurfaceVariant),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}