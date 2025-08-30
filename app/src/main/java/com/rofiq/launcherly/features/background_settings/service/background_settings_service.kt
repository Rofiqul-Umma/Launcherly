package com.rofiq.launcherly.features.background_settings.service

import com.rofiq.launcherly.core.shared_prefs_helper.SharedPrefsHelper
import com.rofiq.launcherly.features.background_settings.model.BackgroundSetting
import com.rofiq.launcherly.features.background_settings.model.BackgroundType
import com.rofiq.launcherly.features.background_settings.model.BackgroundSourceType
import com.rofiq.launcherly.features.background_settings.model.BackgroundDefaults
import javax.inject.Inject

class BackgroundSettingsService @Inject constructor(
    private val sharedPrefsHelper: SharedPrefsHelper
) {
    
    companion object {
        private const val KEY_BACKGROUND_TYPE = "background_type"
        private const val KEY_BACKGROUND_SOURCE_TYPE = "background_source_type"
        private const val KEY_BACKGROUND_PATH = "background_path"
        private const val KEY_BACKGROUND_NAME = "background_name"
    }
    
    fun getCurrentBackground(): BackgroundSetting {
        val defaultBackground = BackgroundDefaults.defaultVideoBackgrounds.first()
        
        val typeStr = sharedPrefsHelper.getString(KEY_BACKGROUND_TYPE, BackgroundType.IMAGE.name)
        val sourceTypeStr = sharedPrefsHelper.getString(KEY_BACKGROUND_SOURCE_TYPE, BackgroundSourceType.LOCAL.name)
        val path = sharedPrefsHelper.getString(KEY_BACKGROUND_PATH, defaultBackground.resourcePath)
        val name = sharedPrefsHelper.getString(KEY_BACKGROUND_NAME, defaultBackground.name)
            
        return BackgroundSetting(
            type = BackgroundType.valueOf(typeStr),
            sourceType = BackgroundSourceType.valueOf(sourceTypeStr),
            resourcePath = path,
            name = name
        )
    }
    
    fun setBackground(backgroundSetting: BackgroundSetting) {
        sharedPrefsHelper.saveString(KEY_BACKGROUND_TYPE, backgroundSetting.type.name)
        sharedPrefsHelper.saveString(KEY_BACKGROUND_SOURCE_TYPE, backgroundSetting.sourceType.name)
        sharedPrefsHelper.saveString(KEY_BACKGROUND_PATH, backgroundSetting.resourcePath)
        sharedPrefsHelper.saveString(KEY_BACKGROUND_NAME, backgroundSetting.name)
    }
    
    fun getAvailableBackgrounds(): List<BackgroundSetting> {
        return BackgroundDefaults.getAllBackgrounds()
    }
}