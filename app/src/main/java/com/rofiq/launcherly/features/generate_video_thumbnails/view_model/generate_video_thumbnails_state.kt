package com.rofiq.launcherly.features.generate_video_thumbnails.view_model

import android.graphics.Bitmap

interface GenerateVideoThumbnailsState

// initial state
object GenerateVideoThumbnailsInitial : GenerateVideoThumbnailsState

// loading
object GenerateVideoThumbnailsLoading : GenerateVideoThumbnailsState

// loaded
data class GenerateVideoThumbnailsLoaded(val thumbnails: List<Bitmap>) : GenerateVideoThumbnailsState

// empty
object GenerateVideoThumbnailsEmpty : GenerateVideoThumbnailsState

// error
data class GenerateVideoThumbnailsError(val message: String) : GenerateVideoThumbnailsState

