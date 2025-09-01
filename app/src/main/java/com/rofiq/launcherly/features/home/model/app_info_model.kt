package com.rofiq.launcherly.features.home.model

import android.content.Intent
import android.graphics.drawable.Drawable

data class AppInfoModel(
    val name: String,
    val packageName: String,
    val icon: Drawable?,
    val launcherIntent: Intent?
)