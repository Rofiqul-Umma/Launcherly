package com.rofiq.launcherly.features.background_settings.model

import com.rofiq.launcherly.utils.GoogleDriveUtils
import com.rofiq.launcherly.features.background_settings.utils.LocalFileUtils

enum class BackgroundType {
    IMAGE,
    VIDEO
}

enum class BackgroundSourceType {
    LOCAL,
    URL
}

data class BackgroundSetting(
    val type: BackgroundType,
    val sourceType: BackgroundSourceType = BackgroundSourceType.LOCAL,
    val resourcePath: String,
    val name: String
) {
    /**
     * Returns the direct URL for loading the resource.
     * For Google Drive sharing URLs, this converts them to direct download URLs.
     * For local files, this returns the file path directly.
     */
    val directUrl: String
        get() = when (sourceType) {
            BackgroundSourceType.URL -> {
                GoogleDriveUtils.convertSharingUrlToDownloadUrl(resourcePath)
            }
            BackgroundSourceType.LOCAL -> {
                // For local files, return the path directly
                resourcePath
            }
        }
    
    companion object {
        /**
         * Creates a BackgroundSetting from a local file URI
         */
        fun fromLocalFile(
            type: BackgroundType,
            filePath: String,
            name: String
        ): BackgroundSetting {
            return BackgroundSetting(
                type = type,
                sourceType = BackgroundSourceType.LOCAL,
                resourcePath = filePath,
                name = name
            )
        }
        
        /**
         * Creates a BackgroundSetting from a URL
         */
        fun fromUrl(
            type: BackgroundType,
            url: String,
            name: String
        ): BackgroundSetting {
            return BackgroundSetting(
                type = type,
                sourceType = BackgroundSourceType.URL,
                resourcePath = url,
                name = name
            )
        }
    }
}

object BackgroundDefaults {
    val defaultVideoBackgrounds = listOf(
        BackgroundSetting(
            type = BackgroundType.VIDEO,
            sourceType = BackgroundSourceType.URL,
            resourcePath = "https://drive.google.com/file/d/1mGJIeS3n7csd4HipY8E41XLJFUpY5gmK/view?usp=drive_link",
            name = "Video"
        ),
        BackgroundSetting(
            type = BackgroundType.VIDEO,
            sourceType = BackgroundSourceType.URL,
            resourcePath = "https://drive.google.com/file/d/1B4oK5z2Zz7Mtmly3I5fCWz7oa6nO0Vg9/view?usp=drive_link",
            name = "Video"
        ),
        BackgroundSetting(
            type = BackgroundType.VIDEO,
            sourceType = BackgroundSourceType.URL,
            resourcePath = "https://drive.google.com/file/d/12USk7T6uqC8QqaaZ_C1u0EngcG-xSIkh/view?usp=drive_link",
            name = "Video"
        )

    )
    
    val defaultImageBackgrounds = listOf(
        BackgroundSetting(
            type = BackgroundType.IMAGE,
            sourceType = BackgroundSourceType.LOCAL,
            resourcePath = "android.resource://com.rofiq.launcherly/raw/default_background_image",
            name = "Image"
        ),
        BackgroundSetting(
            type = BackgroundType.IMAGE,
            sourceType = BackgroundSourceType.URL,
            resourcePath = "https://drive.google.com/file/d/1qSjQ8AGO96ugDPmrH6bZ-Sd2iSpq_CoH/view?usp=drive_link",
            name = "Image"
        ),
        BackgroundSetting(
            type = BackgroundType.IMAGE,
            sourceType = BackgroundSourceType.URL,
            resourcePath = "https://drive.google.com/file/d/18CL8ISw198Z8oeVvw5UpQMWCA6oFEMF7/view?usp=drive_link",
            name = "Image"
        ),
        BackgroundSetting(
            type = BackgroundType.IMAGE,
            sourceType = BackgroundSourceType.URL,
            resourcePath = "https://drive.google.com/file/d/1Z-FulAXZ8eWgC8x2Kc-o2U4UBMSDf8bt/view?usp=drive_link",
            name = "Image"
        ),
        BackgroundSetting(
            type = BackgroundType.IMAGE,
            sourceType = BackgroundSourceType.URL,
            resourcePath = "https://drive.google.com/file/d/1zrcgmBxLKdH_PhzHzBmBArbvdmYG2R6L/view?usp=drive_link",
            name = "Image"
        ),
        BackgroundSetting(
            type = BackgroundType.IMAGE,
            sourceType = BackgroundSourceType.URL,
            resourcePath = "https://drive.google.com/file/d/1lJ1qvmH2RYzqiErO8NVH_5YZEHH8XbQR/view?usp=drive_link",
            name = "Image"
        ),
        BackgroundSetting(
            type = BackgroundType.IMAGE,
            sourceType = BackgroundSourceType.URL,
            resourcePath = "https://drive.google.com/file/d/1Zs4srZl5Uj-PMBoAq9KlFhb4saq42M8X/view?usp=drive_link",
            name = "Image"
        ),

        BackgroundSetting(
            type = BackgroundType.IMAGE,
            sourceType = BackgroundSourceType.URL,
            resourcePath = "https://drive.google.com/file/d/18D6cP1rm85o6VKSMi10QAVhuN94oOZPl/view?usp=drive_link",
            name = "Image"
        ),

        BackgroundSetting(
            type = BackgroundType.IMAGE,
            sourceType = BackgroundSourceType.URL,
            resourcePath = "https://drive.google.com/file/d/112zdoA5hPW6NTbk_Vy23HTDi38Im0EuM/view?usp=drive_link",
            name = "Image"
        ),

        BackgroundSetting(
            type = BackgroundType.IMAGE,
            sourceType = BackgroundSourceType.URL,
            resourcePath = "https://drive.google.com/file/d/1trMTOoJny_I2kSVyh6uh21Vp_MRH3--4/view?usp=drive_link",
            name = "Image"
        ),

        BackgroundSetting(
            type = BackgroundType.IMAGE,
            sourceType = BackgroundSourceType.URL,
            resourcePath = "https://drive.google.com/file/d/19CxFqyaIaeMTtLgGlW0NhNBUDiw_ZcGc/view?usp=drive_link",
            name = "Image"
        ),

        BackgroundSetting(
            type = BackgroundType.IMAGE,
            sourceType = BackgroundSourceType.URL,
            resourcePath = "https://drive.google.com/file/d/1MsCWYFhjSkp89-gzUTFfZaCpfqLIzMtF/view?usp=drive_link",
            name = "Image"
        ),
        BackgroundSetting(
            type = BackgroundType.IMAGE,
            sourceType = BackgroundSourceType.URL,
            resourcePath = "https://drive.google.com/file/d/1Qe3pEnJ5dWOvqFUxa_B8esJRemvd_DbW/view?usp=drive_link",
            name = "Image"
        ),

        BackgroundSetting(
            type = BackgroundType.IMAGE,
            sourceType = BackgroundSourceType.URL,
            resourcePath = "https://drive.google.com/file/d/1Dtvwxzi0ZYuF78JYx9u6GV_-g_a7v0Tq/view?usp=drive_link",
            name = "Image"
        ),

        BackgroundSetting(
            type = BackgroundType.IMAGE,
            sourceType = BackgroundSourceType.URL,
            resourcePath = "https://drive.google.com/file/d/1whkaUZgAwiAnrPqq-JhIEu0wDj5Dudc-/view?usp=drive_link",
            name = "Image"
        ),

        BackgroundSetting(
            type = BackgroundType.IMAGE,
            sourceType = BackgroundSourceType.URL,
            resourcePath = "https://drive.google.com/file/d/1JKf7cxO89ZQsoTten6cnnQOZH6pcN-rk/view?usp=drive_link",
            name = "Image"
        )




    )
    
    fun getAllBackgrounds() : List<BackgroundSetting> {
        return defaultImageBackgrounds + defaultVideoBackgrounds
    }

    fun getListDefaultVideoBackground() : List<BackgroundSetting> {
        return defaultVideoBackgrounds
    }
    
    /**
     * Creates a list of sample local backgrounds for demonstration
     */
    fun getSampleLocalBackgrounds(): List<BackgroundSetting> {
        return listOf(
            BackgroundSetting(
                type = BackgroundType.IMAGE,
                sourceType = BackgroundSourceType.LOCAL,
                resourcePath = "android.resource://com.rofiq.launcherly/raw/default_background_image",
                name = "Default Image"
            )
        )
    }
}