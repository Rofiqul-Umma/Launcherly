package com.rofiq.launcherly.features.launch_app.service

import android.content.Context
import android.content.pm.PackageManager
import javax.inject.Inject

class LaunchAppService @Inject constructor(val context: Context) {

    fun launchApp(packageName: String){
        val pm: PackageManager = context.packageManager
        val intent = pm.getLaunchIntentForPackage(packageName)
        intent?.let { context.startActivity(it) }
    }
}