package com.rofiq.launcherly.features.home.view.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fitInside
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rofiq.launcherly.common.color.TVColors
import com.rofiq.launcherly.common.text_style.TVTypography
import com.rofiq.launcherly.common.widgets.LCircularLoading
import com.rofiq.launcherly.features.home.view_model.HomeErrorFetchAppsState
import com.rofiq.launcherly.features.home.view_model.HomeLoadedFetchAppState
import com.rofiq.launcherly.features.home.view_model.HomeLoadingFetchAppsState
import com.rofiq.launcherly.features.home.view_model.HomeViewModel
import com.rofiq.launcherly.features.launch_app.view_model.LaunchAppViewModel

@Composable
fun ListApps(
    homeVM: HomeViewModel = hiltViewModel(),
    launchAppVM: LaunchAppViewModel = hiltViewModel(),
) {
    val homeState = homeVM.homeState.collectAsState()
    val firstAppFocusRequester = remember { FocusRequester() }

    LaunchedEffect(firstAppFocusRequester) {
        firstAppFocusRequester.requestFocus()
    }

    Box(
        contentAlignment = Alignment.Center, // Center the content within the Box
        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(TVColors.Surface.copy(alpha = 0.4f)) // Optional background for app list area
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
                                    8.dp
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

//                            if (appListFocused.value) {
//                                Spacer(modifier = Modifier.height(8.dp))
//                                Text(
//                                    text = app.name,
//                                    maxLines = 1,
//                                    style = TVTypography.BodySmall
//                                )
//                            }
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