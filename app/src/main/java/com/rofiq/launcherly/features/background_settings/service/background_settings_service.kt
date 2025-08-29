package com.rofiq.launcherly.features.background_settings.service

import com.rofiq.launcherly.core.shared_prefs_helper.SharedPrefsHelper
import com.rofiq.launcherly.features.background_settings.model.BackgroundSetting
import com.rofiq.launcherly.features.background_settings.model.BackgroundType
import com.rofiq.launcherly.features.background_settings.model.BackgroundDefaults
import javax.inject.Inject

class BackgroundSettingsService @Inject constructor(
    private val sharedPrefsHelper: SharedPrefsHelper
) {
    
    companion object {
        private const val KEY_BACKGROUND_TYPE = "background_type"
        private const val KEY_BACKGROUND_PATH = "background_path"
        private const val KEY_BACKGROUND_NAME = "background_name"
    }
    
    fun getCurrentBackground(): BackgroundSetting {
        val defaultBackground = BackgroundDefaults.defaultVideoBackgrounds.first()
        
        val type = sharedPrefsHelper.getString(KEY_BACKGROUND_TYPE, BackgroundType.VIDEO.name)
        val path = sharedPrefsHelper.getString(KEY_BACKGROUND_PATH, defaultBackground.resourcePath)
        val name = sharedPrefsHelper.getString(KEY_BACKGROUND_NAME, defaultBackground.name)
            
        return BackgroundSetting(
            type = BackgroundType.valueOf(type),
            resourcePath = path,
            name = name
        )
    }
    
    fun setBackground(backgroundSetting: BackgroundSetting) {
        sharedPrefsHelper.saveString(KEY_BACKGROUND_TYPE, backgroundSetting.type.name)
        sharedPrefsHelper.saveString(KEY_BACKGROUND_PATH, backgroundSetting.resourcePath)
        sharedPrefsHelper.saveString(KEY_BACKGROUND_NAME, backgroundSetting.name)
    }
    
    fun getAvailableBackgrounds(): List<BackgroundSetting> {
        return BackgroundDefaults.getAllBackgrounds()
    }
}