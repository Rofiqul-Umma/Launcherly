package com.rofiq.launcherly.features.background_settings.model

import com.rofiq.launcherly.utils.GoogleDriveUtils

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
     */
    val directUrl: String
        get() = if (sourceType == BackgroundSourceType.URL) {
            GoogleDriveUtils.convertSharingUrlToDownloadUrl(resourcePath)
        } else {
            resourcePath
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
        )



    )
    
    fun getAllBackgrounds() : List<BackgroundSetting> {
        return defaultImageBackgrounds + defaultVideoBackgrounds
    }

    fun getListDefaultVideoBackground() : List<BackgroundSetting> {
        return defaultVideoBackgrounds
    }
}