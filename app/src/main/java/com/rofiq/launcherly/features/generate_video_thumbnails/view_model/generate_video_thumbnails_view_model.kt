package com.rofiq.launcherly.features.generate_video_thumbnails.view_model

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rofiq.launcherly.features.background_settings.model.BackgroundType
import com.rofiq.launcherly.features.background_settings.service.BackgroundSettingsService
import com.rofiq.launcherly.features.generate_video_thumbnails.service.GenerateVideoThumbnailsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenerateVideoThumbnailsViewModel @Inject constructor
    (
    val service: GenerateVideoThumbnailsService,
) :
    ViewModel() {

    private val _generateVideoThumbState = MutableStateFlow<Bitmap?>(
        null
    )
    val generateVideoThumbState: StateFlow<Bitmap?> =
        _generateVideoThumbState.asStateFlow()


    fun generateThumbnailForVideo(videoPath: String) {
        viewModelScope.launch {
            try {
                val thumbnail = service.generateThumbnail(videoPath)
                _generateVideoThumbState.value = thumbnail
            } catch (e: Exception) {
                _generateVideoThumbState.value = null
            }
        }
    }
}