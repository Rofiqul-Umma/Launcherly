package com.rofiq.launcherly.features.home.view

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import androidx.navigation.NavController
import com.rofiq.launcherly.R
import com.rofiq.launcherly.common.color.TVColors
import com.rofiq.launcherly.common.text_style.TVTypography
import com.rofiq.launcherly.common.widgets.LCircularLoading
import com.rofiq.launcherly.features.background_settings.view.DynamicBackground
import com.rofiq.launcherly.features.check_internet.view_model.CheckInternetIsConnected
import com.rofiq.launcherly.features.check_internet.view_model.CheckInternetViewModel
import com.rofiq.launcherly.features.device_manager.view_model.DeviceManagerViewModel
import com.rofiq.launcherly.features.fetch_date_time.view_model.FetchDateTimeLoading
import com.rofiq.launcherly.features.fetch_date_time.view_model.FetchDateTimeSuccess
import com.rofiq.launcherly.features.fetch_date_time.view_model.FetchDateTimeViewModel
import com.rofiq.launcherly.features.get_account_info.view_model.GetAccountInfoViewModel
import com.rofiq.launcherly.features.home.view.component.ListApps

@OptIn(UnstableApi::class)
@Composable
fun HomePage(
    navController: NavController,
    dateTimeVM: FetchDateTimeViewModel = hiltViewModel(),
    checkInternetVM: CheckInternetViewModel = hiltViewModel(),
    deviceManagerVM: DeviceManagerViewModel = hiltViewModel()
) {


    val fetchDateTimeState = dateTimeVM.fetchDateTimeState.collectAsState()
    val checkInternetState = checkInternetVM.checkInternetState.collectAsState()

    val settingsFocusRequester = remember { FocusRequester() }
    val wifiFocusRequester = remember { FocusRequester() }

    val wifiFocused = remember { mutableStateOf(false) }
    val settingsFocused = remember { mutableStateOf(false) }
    val context = LocalContext.current

    // ExoPlayer is now handled by DynamicBackground component

    Scaffold(
        containerColor = TVColors.Background,
        contentColor = TVColors.OnSurface
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Video Background (Placeholder - You'll need to implement this)
            Box(
                modifier = Modifier.fillMaxSize()
            ) {

                DynamicBackground()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(TVColors.BackgroundOverlay.copy(alpha = 0.5f))
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 32.dp,
                        top = 32.dp,
                        end = 32.dp,
                        bottom = 10.dp
                    ) // General padding for the content
            ) {
                // Top Row: Settings and Profile Icons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Clock and Date
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        when (fetchDateTimeState.value) {
                            is FetchDateTimeLoading -> LCircularLoading()
                            is FetchDateTimeSuccess -> {
                                Text(
                                    text = (fetchDateTimeState.value as FetchDateTimeSuccess).dateTime.time,
                                    style = TVTypography.HeaderLarge.copy(color = TVColors.OnSurface, fontSize = 35.sp)
                                )

                                Text(
                                    text = (fetchDateTimeState.value as FetchDateTimeSuccess).dateTime.date,
                                    style = TVTypography.BodyLarge.copy(color = TVColors.OnSurfaceVariant)
                                )
                            }
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier
                                .size(30.dp)
                                .focusRequester(settingsFocusRequester)
                                .onFocusChanged { settingsFocused.value = it.isFocused }
                                .focusable()
                                .background(
                                    color = if (settingsFocused.value) TVColors.OnSurfaceSecondary.copy(alpha = 0.5f) else Color.Transparent,
                                    shape = RoundedCornerShape(100.dp)
                                )
                                .padding(5.dp)
                                .onKeyEvent { keyEvent ->
                                    if (keyEvent.key == Key.DirectionCenter && keyEvent.type == KeyEventType.KeyUp) {
                                        navController.navigate("guided_settings")
                                        true
                                    } else false
                                },
                            tint = TVColors.OnSurface
                        )

                        Spacer(modifier = Modifier.size(16.dp))

                        Icon(
                            imageVector = if (checkInternetState.value is CheckInternetIsConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                            contentDescription = "Wifi",
                            modifier = Modifier
                                .size(30.dp)
                                .focusRequester(wifiFocusRequester)
                                .onFocusChanged { wifiFocused.value = it.isFocused }
                                .focusable()
                                .background(
                                    color = if (wifiFocused.value) TVColors.OnSurfaceSecondary.copy(alpha = 0.5f) else Color.Transparent,
                                    shape = RoundedCornerShape(100.dp)
                                )
                                .onKeyEvent { keyEvent ->
                                    if (keyEvent.key == Key.DirectionCenter && keyEvent.type == KeyEventType.KeyUp) {
                                        deviceManagerVM.openNetworkSettings()
                                        true
                                    } else false
                                }
                                .padding(5.dp),
                            tint = TVColors.OnSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(2f)) // Pushes app list to the bottom or center

                ListApps()
            }
        }
    }
}

