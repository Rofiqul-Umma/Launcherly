package com.rofiq.launcherly.features.home.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.rofiq.launcherly.features.home.model.AppInfoModel

class HomeService(private val context: Context) {

    fun fetchInstalledApps(): List<AppInfoModel> {
        val apps = mutableListOf<AppInfoModel>()
        try {
            val packageManager = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            val resolveInfoList = packageManager.queryIntentActivities(intent, 0)

            for (resolveInfo in resolveInfoList) {
                val appName = resolveInfo.loadLabel(packageManager).toString()
                val packageName = resolveInfo.activityInfo.packageName
                val icon = resolveInfo.loadIcon(packageManager)
                val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                if (launchIntent != null && packageName != context.packageName ) { // Only add apps that can be launched
                    apps.add(AppInfoModel(appName, packageName, icon, launchIntent))
                }
            }
        } catch (e: Exception) {
            throw Exception("Error fetching installed apps", e)
        }
        return apps.sortedBy { it.name.lowercase() }
    }
}