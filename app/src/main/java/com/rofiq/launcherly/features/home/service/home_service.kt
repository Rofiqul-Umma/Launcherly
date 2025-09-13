package com.rofiq.launcherly.features.home.service

import android.content.Context
import com.rofiq.launcherly.features.favorite_apps.service.FavoriteAppsService
import com.rofiq.launcherly.features.home.model.AppInfoModel

class HomeService(private val context: Context, private val favoriteAppsService: FavoriteAppsService) {

    fun fetchInstalledApps(): List<AppInfoModel> {
        val apps = mutableListOf<AppInfoModel>()
        try {
            val packageManager = context.packageManager
            val resolveInfoList = packageManager.getInstalledPackages(0)

            for (resolveInfo in resolveInfoList) {
                val appInfo = resolveInfo.applicationInfo
                if (appInfo != null) {
                    val appName = appInfo.loadLabel(packageManager).toString()
                    val packageName = appInfo.packageName
                    val icon = appInfo.loadLogo(packageManager) ?: appInfo.loadIcon(packageManager)
                    val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                    if (launchIntent != null && packageName != context.packageName) {
                        apps.add(AppInfoModel(appName, packageName, icon, launchIntent))
                    }
                }
            }
        } catch (e: Exception) {
            throw Exception("Error fetching installed apps", e)
        }
        return apps.sortedBy { it.name.lowercase() }
    }

    fun fetchFavoriteApps(): List<AppInfoModel> {
        val allApps = fetchInstalledApps()
        val favoritePackages = favoriteAppsService.getFavoriteApps().map { it.packageName }.toSet()
        return allApps.filter { it.packageName in favoritePackages }
    }

    fun fetchAllApps(): List<AppInfoModel> {
        return fetchInstalledApps()
    }
}