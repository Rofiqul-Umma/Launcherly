package com.rofiq.launcherly.features.launch_app.service

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import javax.inject.Inject

class LaunchAppService @Inject constructor(val context: Context) {

    fun launchApp(packageName: String){
        try {
            val pm: PackageManager = context.packageManager
            val intent = pm.getLaunchIntentForPackage(packageName)
            if(intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } else {
                throw Exception("App with package name $packageName not found")
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }
}