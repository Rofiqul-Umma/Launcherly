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
            resourcePath = "android.resource://com.rofiq.launcherly/raw/background_video",
            name = "Default Video 1"
        ),
        BackgroundSetting(
            type = BackgroundType.VIDEO,
            resourcePath = "android.resource://com.rofiq.launcherly/raw/background_video_2", 
            name = "Default Video 2"
        )
    )
    
    val defaultImageBackgrounds = listOf(
        BackgroundSetting(
            type = BackgroundType.IMAGE,
            resourcePath = "android.resource://com.rofiq.launcherly/raw/background_image_1",
            name = "Default Image 1"
        ),
        BackgroundSetting(
            type = BackgroundType.IMAGE,
            resourcePath = "android.resource://com.rofiq.launcherly/raw/background_image_2",
            name = "Default Image 2"
        ),
        BackgroundSetting(
            type = BackgroundType.IMAGE,
            resourcePath = "android.resource://com.rofiq.launcherly/raw/background_image_3",
            name = "Default Image 3"
        )


    )
    
    fun getAllBackgrounds() : List<BackgroundSetting> {
        return defaultImageBackgrounds + defaultVideoBackgrounds
    }
}