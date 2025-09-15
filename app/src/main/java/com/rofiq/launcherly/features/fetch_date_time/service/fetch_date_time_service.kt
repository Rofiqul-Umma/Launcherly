package com.rofiq.launcherly.features.fetch_date_time.service

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FetchDateTimeService {

    fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()) // Format for 12-hour time with AM/PM
        return timeFormat.format(calendar.time)
    }

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        // Format for Day of the week, Month abbreviation, and Day of the month
        val dateFormat = SimpleDateFormat("EEEE, MMM dd yyyy", Locale.getDefault())
        val date = dateFormat.format(calendar.time)
        return date
    }
}