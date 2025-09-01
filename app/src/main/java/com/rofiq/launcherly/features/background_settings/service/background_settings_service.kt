package com.rofiq.launcherly.features.background_settings.service

import android.content.Context
import android.net.Uri
import com.rofiq.launcherly.core.shared_prefs_helper.SharedPrefsHelper
import com.rofiq.launcherly.features.background_settings.model.BackgroundSetting
import com.rofiq.launcherly.features.background_settings.model.BackgroundType
import com.rofiq.launcherly.features.background_settings.model.BackgroundSourceType
import com.rofiq.launcherly.features.background_settings.model.BackgroundDefaults
import com.rofiq.launcherly.features.background_settings.utils.LocalFileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    suspend fun getCurrentBackground(): BackgroundSetting {
         return withContext(Dispatchers.IO){
            // Check if any background settings exist
            val hasBackgroundType = sharedPrefsHelper.contains(KEY_BACKGROUND_TYPE)

            // If no background has been set, return the first default video background
            if (!hasBackgroundType) {
                return@withContext BackgroundDefaults.defaultVideoBackgrounds.first()
            }

            // Otherwise, retrieve the saved background settings
            val typeStr = sharedPrefsHelper.getString(KEY_BACKGROUND_TYPE, BackgroundType.VIDEO.name)
            val sourceTypeStr =
                sharedPrefsHelper.getString(KEY_BACKGROUND_SOURCE_TYPE, BackgroundSourceType.URL.name)
            val path = sharedPrefsHelper.getString(
                KEY_BACKGROUND_PATH,
                BackgroundDefaults.defaultVideoBackgrounds.first().resourcePath
            )
            val name = sharedPrefsHelper.getString(
                KEY_BACKGROUND_NAME,
                BackgroundDefaults.defaultVideoBackgrounds.first().name
            )

            return@withContext BackgroundSetting(
                type = BackgroundType.valueOf(typeStr),
                sourceType = BackgroundSourceType.valueOf(sourceTypeStr),
                resourcePath = path,
                name = name
            )
        }
    }

    suspend fun setBackground(backgroundSetting: BackgroundSetting) {
        withContext(Dispatchers.IO) {
            sharedPrefsHelper.saveString(KEY_BACKGROUND_TYPE, backgroundSetting.type.name)
            sharedPrefsHelper.saveString(
                KEY_BACKGROUND_SOURCE_TYPE,
                backgroundSetting.sourceType.name
            )
            sharedPrefsHelper.saveString(KEY_BACKGROUND_PATH, backgroundSetting.resourcePath)
            sharedPrefsHelper.saveString(KEY_BACKGROUND_NAME, backgroundSetting.name)
        }
    }

    /**
     * Saves a local file as a background setting
     */
    suspend fun setLocalBackground(context: Context, uri: Uri, type: BackgroundType): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Copy the file to internal storage
                val filePath = LocalFileUtils.copyFileToInternalStorage(context, uri)
                
                if (filePath != null) {
                    // Get file name for display
                    val fileName = uri.lastPathSegment ?: "Background"
                    val displayName = fileName.substringBeforeLast(".", fileName)
                    
                    // Create background setting
                    val backgroundSetting = BackgroundSetting(
                        type = type,
                        sourceType = BackgroundSourceType.LOCAL,
                        resourcePath = filePath,
                        name = displayName
                    )
                    
                    // Save the background setting
                    setBackground(backgroundSetting)
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    fun getAvailableBackgrounds(): List<BackgroundSetting> {
        return BackgroundDefaults.getAllBackgrounds()
    }
    
    /**
     * Gets all available backgrounds including sample local backgrounds
     */
    fun getAllAvailableBackgrounds(): List<BackgroundSetting> {
        return BackgroundDefaults.getAllBackgrounds() + BackgroundDefaults.getSampleLocalBackgrounds()
    }
}