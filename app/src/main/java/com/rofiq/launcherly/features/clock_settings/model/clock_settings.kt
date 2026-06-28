package com.rofiq.launcherly.features.clock_settings.model

enum class ClockStyle(val pattern: String, val label: String) {
    TWELVE_HOUR("hh:mm a", "12-hour"),
    TWELVE_HOUR_SECONDS("hh:mm:ss a", "12-hour with seconds"),
    TWENTY_FOUR_HOUR("HH:mm", "24-hour"),
    TWENTY_FOUR_HOUR_SECONDS("HH:mm:ss", "24-hour with seconds"),
    TWELVE_HOUR_COMPACT("h:mm a", "12-hour compact");

    fun next(): ClockStyle = entries[(ordinal + 1) % entries.size]
}

enum class DateStyle(val pattern: String, val label: String) {
    FULL("EEEE, MMM dd yyyy", "Full"),
    NUMERIC("dd/MM/yyyy", "Numeric");

    fun next(): DateStyle = entries[(ordinal + 1) % entries.size]
}

data class ClockSettings(
    val clockStyle: ClockStyle,
    val dateStyle: DateStyle,
    val hideDateTime: Boolean
)
