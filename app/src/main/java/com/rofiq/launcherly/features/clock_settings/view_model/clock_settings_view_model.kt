package com.rofiq.launcherly.features.clock_settings.view_model

import androidx.lifecycle.ViewModel
import com.rofiq.launcherly.features.clock_settings.service.ClockSettingsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ClockSettingsViewModel @Inject constructor(
    private val clockSettingsService: ClockSettingsService
) : ViewModel() {

    val settings: StateFlow<com.rofiq.launcherly.features.clock_settings.model.ClockSettings> =
        clockSettingsService.settings

    fun cycleClockStyle() {
        clockSettingsService.setClockStyle(settings.value.clockStyle.next())
    }

    fun cycleDateStyle() {
        clockSettingsService.setDateStyle(settings.value.dateStyle.next())
    }

    fun toggleHideDateTime() {
        clockSettingsService.setHideDateTime(!settings.value.hideDateTime)
    }
}
