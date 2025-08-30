package com.rofiq.launcherly.features.generate_video_thumbnails.service

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.core.net.toUri
import javax.inject.Inject

class GenerateVideoThumbnailsService @Inject constructor(val context: Context) {

    fun generateThumbnail(videoPath: String): Bitmap? {
         var retriever: MediaMetadataRetriever? = null
        return try {
            val videoUri = videoPath.toUri()
            retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, videoUri)
            retriever.getFrameAtTime(5000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        } catch (e: Exception) {
            null
        } finally {
            retriever?.release()
        }
    }
}