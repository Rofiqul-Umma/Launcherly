package com.rofiq.launcherly.features.home.view

import androidx.annotation.OptIn
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import coil.compose.AsyncImage
import com.rofiq.launcherly.R
import com.rofiq.launcherly.common.color.TVColors
import com.rofiq.launcherly.common.text_style.TVTypography
import com.rofiq.launcherly.common.widgets.LCircularLoading
import com.rofiq.launcherly.features.check_internet.view_model.CheckInternetIsConnected
import com.rofiq.launcherly.features.check_internet.view_model.CheckInternetViewModel
import com.rofiq.launcherly.features.fetch_date_time.view_model.FetchDateTimeLoading
import com.rofiq.launcherly.features.fetch_date_time.view_model.FetchDateTimeSuccess
import com.rofiq.launcherly.features.fetch_date_time.view_model.FetchDateTimeViewModel
import com.rofiq.launcherly.features.get_account_info.view_model.GetAccountInfoEmpty
import com.rofiq.launcherly.features.get_account_info.view_model.GetAccountInfoError
import com.rofiq.launcherly.features.get_account_info.view_model.GetAccountInfoSuccess
import com.rofiq.launcherly.features.get_account_info.view_model.GetAccountInfoViewModel
import com.rofiq.launcherly.features.home.view_model.HomeErrorFetchAppsState
import com.rofiq.launcherly.features.home.view_model.HomeLoadedFetchAppState
import com.rofiq.launcherly.features.home.view_model.HomeLoadingFetchAppsState
import com.rofiq.launcherly.features.home.view_model.HomeViewModel
import com.rofiq.launcherly.features.launch_app.view_model.LaunchAppError
import com.rofiq.launcherly.features.launch_app.view_model.LaunchAppLoading
import com.rofiq.launcherly.features.launch_app.view_model.LaunchAppSuccess
import com.rofiq.launcherly.features.launch_app.view_model.LaunchAppViewModel

@OptIn(UnstableApi::class)
@Composable
fun HomePage(
    homeVM: HomeViewModel = hiltViewModel(),
    dateTimeVM: FetchDateTimeViewModel = hiltViewModel(),
    getAccountInfoVM: GetAccountInfoViewModel = hiltViewModel(),
    checkInternetVM: CheckInternetViewModel = hiltViewModel(),
    launchAppVM: LaunchAppViewModel = hiltViewModel()
) {

    val homeState = homeVM.homeState.collectAsState()
    val fetchDateTimeState = dateTimeVM.fetchDateTimeState.collectAsState()
    val getAccountInfoState = getAccountInfoVM.getAccountInfoState.collectAsState()
    val checkInternetState = checkInternetVM.checkInternetState.collectAsState()
    val launchAppState = launchAppVM.launchAppState.collectAsState()

    val settingsFocusRequester = remember { FocusRequester() }
    val profileFocusRequester = remember { FocusRequester() }
    val wifiFocusRequester = remember { FocusRequester() }

    val firstAppFocusRequester = remember { FocusRequester() }

    val wifiFocused = remember { mutableStateOf(false) }
    val settingsFocused = remember { mutableStateOf(false) }
    val profileFocused = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val clickedPackageName by remember { mutableStateOf("") }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem =
                MediaItem.fromUri("android.resource://${context.packageName}/${R.raw.background_video}")
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
            repeatMode = ExoPlayer.REPEAT_MODE_ONE // Loop the video
        }
    }

    LaunchedEffect(Unit) {
        firstAppFocusRequester.requestFocus()
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(exoPlayer, lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> exoPlayer.playWhenReady = true
                Lifecycle.Event.ON_START -> exoPlayer.playWhenReady = true
                Lifecycle.Event.ON_PAUSE -> exoPlayer.playWhenReady = false
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
                AndroidView(
                    factory = { context ->
                        PlayerView(context).apply {
                            player = exoPlayer
                            useController = false
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        }
                    }
                )

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
                                color = if (settingsFocused.value) TVColors.Surface.copy(alpha = 0.5f) else Color.Transparent,
                                shape = RoundedCornerShape(100.dp)
                            )
                            .padding(5.dp),
                        tint = TVColors.OnSurface
                    )

                    Spacer(modifier = Modifier.size(16.dp))

                    when (getAccountInfoState.value) {
                        is GetAccountInfoEmpty, is GetAccountInfoError -> Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(30.dp)
                                .focusRequester(profileFocusRequester)
                                .onFocusChanged { profileFocused.value = it.isFocused }
                                .focusable()
                                .background(
                                    color = if (profileFocused.value) TVColors.Surface.copy(alpha = 0.5f) else Color.Transparent,
                                    shape = RoundedCornerShape(100.dp)
                                )
                                .padding(5.dp),
                            tint = TVColors.OnSurface
                        )

                        is GetAccountInfoSuccess ->
                            AsyncImage(
                                model = (getAccountInfoState.value as GetAccountInfoSuccess).accountInfo.profilePictureUri,
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .size(30.dp)
                                    .focusRequester(profileFocusRequester)
                                    .onFocusChanged { profileFocused.value = it.isFocused }
                                    .focusable()
                                    .background(
                                        color = if (profileFocused.value) TVColors.Surface.copy(
                                            alpha = 0.5f
                                        ) else Color.Transparent,
                                        shape = RoundedCornerShape(100.dp)
                                    )
                                    .padding(5.dp),
                            )
                    }

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
                                color = if (wifiFocused.value) TVColors.Surface.copy(alpha = 0.5f) else Color.Transparent,
                                shape = RoundedCornerShape(100.dp)
                            )
                            .padding(5.dp),
                        tint = TVColors.OnSurface
                    )

                }

                Spacer(modifier = Modifier.height(32.dp))

                // Clock and Date
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    when (fetchDateTimeState.value) {
                        is FetchDateTimeLoading -> LCircularLoading()
                        is FetchDateTimeSuccess -> {
                            Text(
                                text = (fetchDateTimeState.value as FetchDateTimeSuccess).dateTime.time,
                                style = TVTypography.HeaderLarge.copy(color = TVColors.OnSurface)
                            )

                            Text(
                                text = (fetchDateTimeState.value as FetchDateTimeSuccess).dateTime.date,
                                style = TVTypography.BodyLarge.copy(color = TVColors.OnSurfaceVariant)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(2f)) // Pushes app list to the bottom or center

                // Horizontal List of Apps (Placeholder)
                Box(
                    contentAlignment = Alignment.Center, // Center the content within the Box
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(TVColors.Surface.copy(alpha = 0.5f)) // Optional background for app list area
                ) {
                    when (homeState.value) {
                        is HomeLoadingFetchAppsState -> {
                            LCircularLoading()
                        }

                        is HomeLoadedFetchAppState -> {
                            val apps = (homeState.value as HomeLoadedFetchAppState).apps
                            LazyRow(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            )
                            {
                                items(apps) { app ->
                                    val isFirst = apps.indexOf(app) == 0
                                    val appListFocusRequester =
                                        if (isFirst) firstAppFocusRequester else remember { FocusRequester() }
                                    val appListFocused = remember { mutableStateOf(false) }
                                    val imageSize = animateDpAsState(
                                        targetValue = if (appListFocused.value) 60.dp else 55.dp,
                                        label = "AppIconSize"
                                    )
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .padding(
                                                top = 8.dp,
                                                bottom = 8.dp,
                                                start = 15.dp,
                                                end = 15.dp
                                            )
                                            .focusRequester(appListFocusRequester)
                                            .onFocusChanged { appListFocused.value = it.isFocused }
                                            .focusable()
                                            .onKeyEvent { event ->
                                                if (event.type == KeyEventType.KeyUp &&
                                                    (event.key == Key.Enter || event.key == Key.NumPadEnter || event.key == Key.DirectionCenter)
                                                ) {
                                                    launchAppVM.launchApp(app.packageName)
                                                    true
                                                } else {
                                                    false
                                                }
                                            }
                                    ) {
                                        AsyncImage(
                                            model = app.icon,
                                            contentDescription = app.name,
                                            modifier = Modifier
                                                .size(imageSize.value)
                                                .drawBehind {
                                                    if (appListFocused.value) {
                                                        drawRoundRect(
                                                            color = TVColors.OnSurfaceVariant,
                                                            size = size,
                                                            cornerRadius = CornerRadius(12.dp.toPx()),
                                                        )
                                                    }
                                                }
                                                .padding(10.dp)
                                        )
                                        if (appListFocused.value) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = app.name,
                                                maxLines = 1,
                                                style = TVTypography.BodySmall
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        is HomeErrorFetchAppsState -> {
                            Text(
                                text = (homeState.value as HomeErrorFetchAppsState).message,
                                style = TVTypography.BodyLarge.copy(
                                    color = TVColors.OnSurface,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }

                        else -> {
                            Text(
                                text = "No apps found",
                                style = TVTypography.BodyLarge.copy(
                                    color = TVColors.OnSurface,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                    // Implement your LazyRow or other horizontal list here
                }
            }
        }
    }
}

