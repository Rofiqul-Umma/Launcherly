package com.rofiq.launcherly.features.guided_settings.view

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rofiq.launcherly.common.color.TVColors
import com.rofiq.launcherly.common.text_style.TVTypography
import com.rofiq.launcherly.features.device_manager.view_model.DeviceManagerViewModel

data class SettingsItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val action: () -> Unit
)

@Composable
fun GuidedSettingsStep(
    onBack: () -> Unit,
    deviceManagerVM: DeviceManagerViewModel = hiltViewModel(),
    navController: NavController
) {
    val settingsItems = listOf(
        SettingsItem(
            title = "System Settings",
            description = "Open device system settings",
            icon = Icons.Default.Settings,
            action = { deviceManagerVM.openSystemSettings() }
        ),
        SettingsItem(
            title = "Background Settings",
            description = "Change wallpaper and background",
            icon = Icons.Default.Wallpaper,
            action = { navController.navigate("background_settings") } // You can change this to background settings
        )
    )

    GuidedStepLayout(
        title = "Settings",
        description = "Choose a setting to configure",
        onBack = onBack
    ) {
        SettingsButtonList(
            items = settingsItems,
            onBack = onBack
        )
    }
}

@Composable
fun GuidedStepLayout(
    title: String,
    description: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TVColors.Background)
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = TVTypography.HeaderLarge.copy(color = TVColors.OnSurface),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = description,
                    style = TVTypography.BodyRegular.copy(color = TVColors.OnSurfaceVariant),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Content
            Box(modifier = Modifier.weight(1f)) {
                content()
            }
        }

//        // Back button
//        BackButton(
//            onClick = onBack,
//            modifier = Modifier.align(Alignment.BottomStart)
//        )
    }
}

@Composable
fun SettingsButtonList(
    items: List<SettingsItem>,
    onBack: () -> Unit
) {
    var focusedIndex by remember { mutableIntStateOf(0) }
    val focusRequesters = remember { items.map { FocusRequester() } }

    LaunchedEffect(Unit) {
        focusRequesters.firstOrNull()?.requestFocus()
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(items) { index, item ->
            SettingsButton(
                item = item,
                isFocused = focusedIndex == index,
                focusRequester = focusRequesters[index],
                onFocusChanged = { focused ->
                    if (focused) focusedIndex = index
                },
                onNavigateUp = {
                    if (index > 0) {
                        focusRequesters[index - 1].requestFocus()
                    }
                },
                onNavigateDown = {
                    if (index < items.size - 1) {
                        focusRequesters[index + 1].requestFocus()
                    }
                },
                onBack = onBack
            )
        }
    }
}

@Composable
fun SettingsButton(
    item: SettingsItem,
    isFocused: Boolean,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateDown: () -> Unit,
    onBack: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(80.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { onFocusChanged(it.isFocused) }
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyUp) {
                    when (keyEvent.key) {
                        Key.DirectionCenter -> {
                            item.action()
                            true
                        }
                        Key.DirectionUp -> {
                            onNavigateUp()
                            true
                        }
                        Key.DirectionDown -> {
                            onNavigateDown()
                            true
                        }
                        Key.Back -> {
                            onBack()
                            true
                        }
                        else -> false
                    }
                } else false
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isFocused)
                TVColors.OnSurfaceSecondary.copy(alpha = 0.3f)
            else
                TVColors.Surface.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = TVColors.OnSurface,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = item.title,
                    style = TVTypography.BodyLarge.copy(color = TVColors.OnSurface)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.description,
                    style = TVTypography.BodyRegular.copy(color = TVColors.OnSurfaceVariant)
                )
            }
        }
    }
}

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .size(56.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.key == Key.DirectionCenter && keyEvent.type == KeyEventType.KeyUp) {
                    onClick()
                    true
                } else false
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isFocused)
                TVColors.OnSurfaceSecondary.copy(alpha = 0.5f)
            else
                TVColors.Surface.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(28.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = TVColors.OnSurface,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}