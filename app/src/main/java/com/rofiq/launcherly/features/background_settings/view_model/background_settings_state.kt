package com.rofiq.launcherly.features.background_settings.view_model

import com.rofiq.launcherly.features.background_settings.model.BackgroundSetting
import com.rofiq.launcherly.features.background_settings.model.MediaItemModel

interface BackgroundSettingsState

object BackgroundSettingsInitial : BackgroundSettingsState

object BackgroundSettingsLoading : BackgroundSettingsState

data class BackgroundSettingsLoaded(
    val availableBackgrounds: List<BackgroundSetting>,
    val currentBackground: BackgroundSetting
) : BackgroundSettingsState

data class BackgroundSettingsError(val message: String) : BackgroundSettingsState


object  FetchMediaStoreLoading: BackgroundSettingsState

data class FetchMediaStoreSuccess(val data: List<MediaItemModel>): BackgroundSettingsState

object FetchMediaStoreEmpty: BackgroundSettingsState

data class FetchMediaStoreError(val message: String): BackgroundSettingsState