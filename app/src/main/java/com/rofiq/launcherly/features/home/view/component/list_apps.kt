package com.rofiq.launcherly.features.home.view.component

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.rofiq.launcherly.common.color.TVColors
import com.rofiq.launcherly.common.text_style.TVTypography
import com.rofiq.launcherly.common.widgets.LoadingIndicator
import com.rofiq.launcherly.features.home.view_model.HomeEmptyFetchAppsState
import com.rofiq.launcherly.features.home.view_model.HomeErrorFetchAppsState
import com.rofiq.launcherly.features.home.view_model.HomeLoadedFetchAppState
import com.rofiq.launcherly.features.home.view_model.HomeLoadingFetchAppsState
import com.rofiq.launcherly.features.home.view_model.HomeViewModel
import com.rofiq.launcherly.features.launch_app.view_model.LaunchAppError
import com.rofiq.launcherly.features.launch_app.view_model.LaunchAppLoading
import com.rofiq.launcherly.features.launch_app.view_model.LaunchAppSuccess
import com.rofiq.launcherly.features.launch_app.view_model.LaunchAppViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.core.graphics.createBitmap

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ListApps(
    homeVM: HomeViewModel = hiltViewModel(),
    launchAppVM: LaunchAppViewModel = hiltViewModel(),
) {
    val homeState = homeVM.homeState.collectAsState()
    val launchAppState by launchAppVM.launchAppState.collectAsState()
    val firstAppFocusRequester = remember { FocusRequester() }

    LaunchedEffect(firstAppFocusRequester) {
        firstAppFocusRequester.requestFocus()
        homeVM.fetchFavoriteApps()
    }

    when (launchAppState) {
        is LaunchAppLoading -> {}
        is LaunchAppSuccess -> {}
        is LaunchAppError -> {
            Log.e("LAUNCH_APP_ERROR", (launchAppState as LaunchAppError).message)
        }
    }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    if (homeState.value is HomeEmptyFetchAppsState) Color.Transparent else TVColors.Surface.copy(
                        alpha = 0.4f
                    )
                )
        ) {
            when (homeState.value) {
                is HomeLoadingFetchAppsState -> {
                    LoadingIndicator()
                }

                is HomeLoadedFetchAppState -> {
                    val apps = (homeState.value as HomeLoadedFetchAppState).apps
                    LazyRow(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        itemsIndexed(
                            apps,
                            key = { _, app -> app.packageName }
                        ) { index, app ->
                            val isFirst = index == 0
                            val isLast = index == apps.lastIndex
                            val appListFocusRequester =
                                if (isFirst) firstAppFocusRequester else remember { FocusRequester() }
                            val appListFocused = remember { mutableStateOf(false) }
                            // Scale the icon via graphicsLayer (draw-phase only) instead of
                            // animating layout size. Animating Modifier.size() forces the LazyRow
                            // to re-measure every frame, which fights the scroll and causes jank.
                            // graphicsLayer keeps layout fixed so scrolling stays smooth.
                            val iconScale by animateFloatAsState(
                                targetValue = if (appListFocused.value) 1f else 70f / 90f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                ),
                                label = "AppIconScale"
                            )
                            // Soft glow halo alpha — spring-animated for a fluid fade in/out
                            val glowAlpha by animateFloatAsState(
                                targetValue = if (appListFocused.value) 1f else 0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                ),
                                label = "AppIconGlow"
                            )
                            // Extract dominant color from the icon drawable once per app.
                            // Falls back to OnSurface (white) while loading or if extraction fails.
                            // IMPORTANT: use a fresh copy of the drawable (constantState.newDrawable())
                            // — never mutate the shared instance, since AsyncImage reads it on the
                            // main thread. Drawables aren't thread-safe and racing on bounds will
                            // crash AdaptiveIconDrawable's mask computation.
                            var dominantColor by remember(app.packageName) {
                                mutableStateOf<Color?>(null)
                            }
                            LaunchedEffect(app.icon) {
                                val drawable = app.icon ?: return@LaunchedEffect
                                val constantState = drawable.constantState ?: return@LaunchedEffect
                                val color = withContext(Dispatchers.Default) {
                                    runCatching {
                                        val source = constantState.newDrawable()
                                        val bitmap = createBitmap(96, 96)
                                        val canvas = Canvas(bitmap)
                                        source.setBounds(0, 0, 96, 96)
                                        source.draw(canvas)
                                        val palette = Palette.from(bitmap).generate()
                                        Color(palette.getDominantColor(TVColors.OnSurface.toArgb()))
                                    }.getOrDefault(TVColors.OnSurface)
                                }
                                dominantColor = color
                            }
                            val glowColor = dominantColor ?: TVColors.OnSurface
                            // Hoist the gradient brush so it's rebuilt only when the dominant color
                            // changes, not on every draw frame during scroll (avoids GC churn). The
                            // animated glowAlpha is applied via drawCircle's alpha parameter instead.
                            val glowBrush = remember(glowColor) {
                                Brush.radialGradient(
                                    colors = listOf(
                                        glowColor.copy(alpha = 0.55f),
                                        glowColor.copy(alpha = 0.18f),
                                        Color.Transparent
                                    )
                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .drawBehind {
                                        if (glowAlpha > 0.01f) {
                                            drawCircle(
                                                brush = glowBrush,
                                                center = center,
                                                radius = size.minDimension / 2f,
                                                alpha = glowAlpha
                                            )
                                        }
                                    }
                                    .padding(8.dp)
                                    .focusRequester(appListFocusRequester)
                                    .onFocusChanged { appListFocused.value = it.isFocused }
                                    .focusable()
                                    .onKeyEvent { keyEvent ->
                                        when {
                                            (keyEvent.key == Key.DirectionCenter || keyEvent.key == Key.Enter) && keyEvent.type == KeyEventType.KeyUp -> {
                                                launchAppVM.launchApp(app.packageName)
                                                true
                                            }

                                            keyEvent.type == KeyEventType.KeyDown &&
                                                    ((keyEvent.key == Key.DirectionLeft && isFirst) ||
                                                            (keyEvent.key == Key.DirectionRight && isLast)) -> true

                                            else -> false
                                        }
                                    }
                            ) {
                                AsyncImage(
                                    model = app.icon,
                                    contentDescription = app.name,
                                    modifier = Modifier
                                        .size(90.dp)
                                        .padding(10.dp)
                                        .graphicsLayer {
                                            scaleX = iconScale
                                            scaleY = iconScale
                                        }
                                )
                                AnimatedVisibility(
                                    visible = appListFocused.value,
                                    enter = fadeIn() + expandVertically(),
                                    exit = fadeOut() + shrinkVertically()
                                ) {
                                    Text(
                                        text = app.name,
                                        style = TVTypography.BodySmall.copy(
                                            color = TVColors.OnSurface,
                                            textAlign = TextAlign.Center
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .padding(top = 2.dp, bottom = 4.dp)
                                            .widthIn(max = 110.dp)
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
        }
}
