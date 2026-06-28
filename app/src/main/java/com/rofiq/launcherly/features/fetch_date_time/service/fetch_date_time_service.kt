package com.rofiq.launcherly.features.fetch_date_time.service

import com.rofiq.launcherly.features.clock_settings.service.ClockSettingsService
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FetchDateTimeService(
    private val clockSettingsService: ClockSettingsService
) {

    fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val pattern = clockSettingsService.settings.value.clockStyle.pattern
        val timeFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return timeFormat.format(calendar.time)
    }

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val pattern = clockSettingsService.settings.value.dateStyle.pattern
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}
