package com.rofiq.launcherly.features.background_settings.view_model

import com.rofiq.launcherly.features.background_settings.model.BackgroundSetting

sealed class BackgroundSettingsState

object BackgroundSettingsInitial : BackgroundSettingsState()

object BackgroundSettingsLoading : BackgroundSettingsState()

data class BackgroundSettingsLoaded(
    val availableBackgrounds: List<BackgroundSetting>,
    val currentBackground: BackgroundSetting
) : BackgroundSettingsState()

data class BackgroundSettingsError(val message: String) : BackgroundSettingsState()