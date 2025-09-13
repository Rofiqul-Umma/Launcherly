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
import androidx.compose.material.icons.filled.Apps
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
import com.rofiq.launcherly.features.home.view_model.HomeViewModel

data class SettingsItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val action: () -> Unit
)

@Composable
fun GuidedSettingsStep(
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
            action = { navController.navigate("background_settings") }
        ),
        SettingsItem(
            title = "Favorite Apps",
            description = "Select apps to display on home screen",
            icon = Icons.Default.Apps,
            action = { navController.navigate("favorite_apps_settings") }
        )
    )

    GuidedStepLayout(
        title = "Settings",
        description = "Choose a setting to configure",
    ) {
        SettingsButtonList(
            items = settingsItems
        )
    }
}

@Composable
fun GuidedStepLayout(
    title: String,
    description: String,
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
    }
}

@Composable
fun SettingsButtonList(
    items: List<SettingsItem>
) {
    val focusRequesters = remember { items.map { FocusRequester() } }

    LaunchedEffect(Unit) {
        if (focusRequesters.isNotEmpty()) {
            focusRequesters.firstOrNull()?.requestFocus()
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(items) { index, item ->
            val nextFocusRequester = if (index < items.size - 1) focusRequesters[index + 1] else null
            val prevFocusRequester = if (index > 0) focusRequesters[index - 1] else null
            
            SettingsButton(
                item = item,
                focusRequester = focusRequesters[index],
                onNextFocus = { nextFocusRequester?.requestFocus() },
                onPrevFocus = { prevFocusRequester?.requestFocus() }
            )
        }
    }
}

@Composable
fun SettingsButton(
    item: SettingsItem,
    focusRequester: FocusRequester,
    onNextFocus: () -> Unit,
    onPrevFocus: () -> Unit,
) {
    var isFocused by remember { mutableIntStateOf(0) } // 0 = not focused, 1 = focused

    Card(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(80.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { 
                isFocused = if (it.isFocused) 1 else 0
            }
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    when (keyEvent.key) {
                        Key.DirectionCenter, Key.Enter -> {
                            item.action()
                            true
                        }

                        Key.DirectionDown -> {
                            onNextFocus()
                            true
                        }

                        Key.DirectionUp -> {
                            onPrevFocus()
                            true
                        }

                        else -> false
                    }
                } else false
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isFocused == 1)
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
