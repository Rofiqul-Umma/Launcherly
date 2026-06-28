package com.rofiq.launcherly.features.clock_settings.service

import com.rofiq.launcherly.core.shared_prefs_helper.SharedPrefsHelper
import com.rofiq.launcherly.features.clock_settings.model.ClockSettings
import com.rofiq.launcherly.features.clock_settings.model.ClockStyle
import com.rofiq.launcherly.features.clock_settings.model.DateStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ClockSettingsService(private val sharedPrefs: SharedPrefsHelper) {

    private val _settings = MutableStateFlow(loadFromPrefs())
    val settings: StateFlow<ClockSettings> = _settings.asStateFlow()

    fun setClockStyle(style: ClockStyle) {
        sharedPrefs.saveString(KEY_CLOCK_STYLE, style.name)
        _settings.value = _settings.value.copy(clockStyle = style)
    }

    fun setDateStyle(style: DateStyle) {
        sharedPrefs.saveString(KEY_DATE_STYLE, style.name)
        _settings.value = _settings.value.copy(dateStyle = style)
    }

    fun setHideDateTime(hide: Boolean) {
        sharedPrefs.saveBoolean(KEY_HIDE_DATE_TIME, hide)
        _settings.value = _settings.value.copy(hideDateTime = hide)
    }

    private fun loadFromPrefs(): ClockSettings {
        val clockStyle = runCatching {
            ClockStyle.valueOf(sharedPrefs.getString(KEY_CLOCK_STYLE, ClockStyle.TWELVE_HOUR.name))
        }.getOrDefault(ClockStyle.TWELVE_HOUR)
        val dateStyle = runCatching {
            DateStyle.valueOf(sharedPrefs.getString(KEY_DATE_STYLE, DateStyle.FULL.name))
        }.getOrDefault(DateStyle.FULL)
        val hideDateTime = sharedPrefs.getBoolean(KEY_HIDE_DATE_TIME, false)
        return ClockSettings(clockStyle, dateStyle, hideDateTime)
    }

    private companion object {
        const val KEY_CLOCK_STYLE = "clock_style"
        const val KEY_DATE_STYLE = "date_style"
        const val KEY_HIDE_DATE_TIME = "hide_date_time"
    }
}
