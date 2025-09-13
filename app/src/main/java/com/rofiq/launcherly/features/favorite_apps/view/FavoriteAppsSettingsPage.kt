package com.rofiq.launcherly.features.favorite_apps.view

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.rofiq.launcherly.common.color.TVColors
import com.rofiq.launcherly.common.text_style.TVTypography
import com.rofiq.launcherly.features.favorite_apps.view_model.AllAppsErrorState
import com.rofiq.launcherly.features.favorite_apps.view_model.AllAppsLoadedState
import com.rofiq.launcherly.features.favorite_apps.view_model.FavoriteAppsLoadedState
import com.rofiq.launcherly.features.favorite_apps.view_model.FavoriteAppsViewModel
import com.rofiq.launcherly.features.guided_settings.view.GuidedStepLayout

@Composable
fun FavoriteAppsSettingsPage(
    navController: NavController,
    favoriteAppsVM: FavoriteAppsViewModel = hiltViewModel()
) {
    val favoriteAppsState by favoriteAppsVM.favoriteAppsState.collectAsState()
    val allAppsState by favoriteAppsVM.allAppsState.collectAsState()

    LaunchedEffect(Unit) {
        favoriteAppsVM.fetchFavoriteApps()
        favoriteAppsVM.fetchAllApps()
    }

    GuidedStepLayout(
        title = "Favorite Apps",
        description = "Select apps to display on home screen"
    ) {
        when (allAppsState) {
            is AllAppsLoadedState -> {
                val apps = (allAppsState as AllAppsLoadedState).apps
                val favoritePackages =
                    if (favoriteAppsState is FavoriteAppsLoadedState) {
                        (favoriteAppsState as FavoriteAppsLoadedState).apps
                            .map { it.packageName }
                            .toSet()
                    } else {
                        emptySet()
                    }

                FavoriteAppsList(
                    apps = apps,
                    favoritePackages = favoritePackages,
                    onToggleFavorite = { packageName ->
                        // Find the app to add/remove
                        val appToAdd = apps.find { it.packageName == packageName }
                        if (appToAdd != null) {
                            if (packageName in favoritePackages) {
                                favoriteAppsVM.removeFavoriteApp(packageName)
                            } else {
                                favoriteAppsVM.addFavoriteApp(appToAdd)
                            }
                        }
                    }
                )
            }

            is AllAppsErrorState -> {
                Text(
                    text = (allAppsState as AllAppsErrorState).message,
                    style = TVTypography.BodyLarge.copy(color = TVColors.OnSurface),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            else -> {
                // Loading or empty state
                Text(
                    text = "Loading apps...",
                    style = TVTypography.BodyLarge.copy(color = TVColors.OnSurface),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun FavoriteAppsList(
    apps: List<com.rofiq.launcherly.features.home.model.AppInfoModel>,
    favoritePackages: Set<String>,
    onToggleFavorite: (String) -> Unit
) {
    val focusRequesters = remember { apps.map { FocusRequester() } }

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
        itemsIndexed(apps) { index, app ->
            val isFavorite = app.packageName in favoritePackages
            val nextFocusRequester = if (index < apps.size - 1) focusRequesters[index + 1] else null
            val prevFocusRequester = if (index > 0) focusRequesters[index - 1] else null
            
            FavoriteAppItem(
                app = app,
                isFavorite = isFavorite,
                focusRequester = focusRequesters[index],
                onToggleFavorite = { onToggleFavorite(app.packageName) },
                onNextFocus = { nextFocusRequester?.requestFocus() },
                onPrevFocus = { prevFocusRequester?.requestFocus() }
            )
        }
    }
}

@Composable
fun FavoriteAppItem(
    app: com.rofiq.launcherly.features.home.model.AppInfoModel,
    isFavorite: Boolean,
    focusRequester: FocusRequester,
    onToggleFavorite: () -> Unit,
    onNextFocus: () -> Unit,
    onPrevFocus: () -> Unit
) {
    var isFocused by remember { mutableIntStateOf(0) } // 0 = not focused, 1 = focused

    Card(
        modifier = Modifier
            .fillMaxWidth(0.8f)
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
                            onToggleFavorite()
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = app.icon,
                    contentDescription = app.name,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = app.name,
                    style = TVTypography.BodyLarge.copy(color = TVColors.OnSurface),
                    maxLines = 1
                )
            }

            Icon(
                imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                tint = if (isFavorite) TVColors.OnSurface else TVColors.OnSurfaceVariant,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onToggleFavorite() }
            )
        }
    }
}