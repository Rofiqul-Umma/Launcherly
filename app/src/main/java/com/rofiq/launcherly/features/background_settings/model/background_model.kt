package com.rofiq.launcherly.features.background_settings.model

enum class BackgroundType {
    IMAGE,
    VIDEO
}

data class BackgroundSetting(
    val type: BackgroundType,
    val resourcePath: String,
    val name: String
)

object BackgroundDefaults {
    val defaultVideoBackgrounds = listOf(
        BackgroundSetting(
            type = BackgroundType.VIDEO,
            resourcePath = "android.resource://com.rofiq.launcherly/raw/background_video_1",
            name = "Video"
        ),
        BackgroundSetting(
            type = BackgroundType.VIDEO,
            resourcePath = "android.resource://com.rofiq.launcherly/raw/background_video_2", 
            name = "Video"
        ),
        BackgroundSetting(
            type = BackgroundType.VIDEO,
            resourcePath = "android.resource://com.rofiq.launcherly/raw/background_video_3",
            name = "Video"
        )

    )
    
    val defaultImageBackgrounds = listOf(
        BackgroundSetting(
            type = BackgroundType.IMAGE,
            resourcePath = "android.resource://com.rofiq.launcherly/raw/background_image_1",
            name = "Image"
        ),
        BackgroundSetting(
            type = BackgroundType.IMAGE,
            resourcePath = "android.resource://com.rofiq.launcherly/raw/background_image_2",
            name = "Image"
        ),
        BackgroundSetting(
            type = BackgroundType.IMAGE,
            resourcePath = "android.resource://com.rofiq.launcherly/raw/background_image_3",
            name = "Image"
        ),
        BackgroundSetting(
            type = BackgroundType.IMAGE,
            resourcePath = "android.resource://com.rofiq.launcherly/raw/background_image_4",
            name = "Image"
        ),
        BackgroundSetting(
            type = BackgroundType.IMAGE,
            resourcePath = "android.resource://com.rofiq.launcherly/raw/background_image_5",
            name = "Image"
        ),
        BackgroundSetting(
            type = BackgroundType.IMAGE,
            resourcePath = "android.resource://com.rofiq.launcherly/raw/background_image_6",
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