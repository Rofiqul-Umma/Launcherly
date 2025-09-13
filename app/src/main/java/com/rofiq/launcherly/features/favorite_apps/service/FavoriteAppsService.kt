package com.rofiq.launcherly.features.favorite_apps.service

import android.content.Context
import android.content.SharedPreferences
import com.rofiq.launcherly.features.favorite_apps.model.FavoriteAppModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoriteAppsService(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("favorite_apps", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val FAVORITE_APPS_KEY = "favorite_apps_list"

    fun getFavoriteApps(): List<FavoriteAppModel> {
        val json = sharedPreferences.getString(FAVORITE_APPS_KEY, "[]")
        val type = object : TypeToken<List<FavoriteAppModel>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun addFavoriteApp(app: FavoriteAppModel) {
        val currentFavorites = getFavoriteApps().toMutableList()
        // Check if app is already in favorites
        if (!currentFavorites.any { it.packageName == app.packageName }) {
            currentFavorites.add(app)
            saveFavoriteApps(currentFavorites)
        }
    }

    fun removeFavoriteApp(packageName: String) {
        val currentFavorites = getFavoriteApps().toMutableList()
        currentFavorites.removeAll { it.packageName == packageName }
        saveFavoriteApps(currentFavorites)
    }

    fun isFavoriteApp(packageName: String): Boolean {
        return getFavoriteApps().any { it.packageName == packageName }
    }

    fun setFavoriteApps(apps: List<FavoriteAppModel>) {
        saveFavoriteApps(apps)
    }

    private fun saveFavoriteApps(apps: List<FavoriteAppModel>) {
        val json = gson.toJson(apps)
        sharedPreferences.edit().putString(FAVORITE_APPS_KEY, json).apply()
    }

    fun clearAllFavorites() {
        sharedPreferences.edit().remove(FAVORITE_APPS_KEY).apply()
    }
}