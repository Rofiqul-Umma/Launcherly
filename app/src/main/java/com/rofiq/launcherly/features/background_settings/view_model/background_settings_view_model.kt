package com.rofiq.launcherly.features.background_settings.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rofiq.launcherly.features.background_settings.model.BackgroundSetting
import com.rofiq.launcherly.features.background_settings.service.BackgroundSettingsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BackgroundSettingsViewModel @Inject constructor(
    private val backgroundSettingsService: BackgroundSettingsService
) : ViewModel() {

    private val _backgroundSettingsState =
        MutableStateFlow<BackgroundSettingsState>(BackgroundSettingsInitial)
    val backgroundSettingsState: StateFlow<BackgroundSettingsState> =
        _backgroundSettingsState.asStateFlow()

    init {
        loadBackgroundSettings()
    }

    fun emit(state: BackgroundSettingsState) {
        viewModelScope.launch {
            _backgroundSettingsState.emit(state)
        }
    }

    private fun loadBackgroundSettings() {
        viewModelScope.launch {
            try {
                emit(BackgroundSettingsLoading)

                val availableBackgrounds = backgroundSettingsService.getAvailableBackgrounds()
                val currentBackground = backgroundSettingsService.getCurrentBackground()

                emit(
                    BackgroundSettingsLoaded(
                        availableBackgrounds = availableBackgrounds,
                        currentBackground = currentBackground
                    )
                )
            } catch (e: Exception) {
                android.util.Log.e(
                    "BackgroundSettingsViewModel",
                    "Error loading background settings",
                    e
                )
                emit(BackgroundSettingsError(e.message ?: "Error loading background settings"))
            }
        }
    }

    fun setBackground(backgroundSetting: BackgroundSetting) {
        viewModelScope.launch {
            try {
                backgroundSettingsService.setBackground(backgroundSetting)
                loadBackgroundSettings()
            } catch (e: Exception) {
                emit(BackgroundSettingsError(e.message ?: "Error setting background"))
            }
        }
    }

    fun getCurrentBackground(): BackgroundSetting {
        val background = backgroundSettingsService.getCurrentBackground()
        return background
    }
}