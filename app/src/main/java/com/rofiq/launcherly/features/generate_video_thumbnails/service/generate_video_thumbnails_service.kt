package com.rofiq.launcherly.features.generate_video_thumbnails.service

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.core.net.toUri
import javax.inject.Inject

class GenerateVideoThumbnailsService @Inject constructor(val context: Context) {
    private val TAG = "VideoThumbnailService"

    fun generateThumbnail(videoPath: String): Bitmap? {
        var retriever: MediaMetadataRetriever? = null
        return try {
            if (videoPath.contains("drive.google.com")) {
                return null
            }
            val videoUri = videoPath.toUri()
            retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, videoUri)
            val thumbnail = retriever.getFrameAtTime(5000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            thumbnail
        } catch (e: Exception) {
            Log.e(TAG, "Error generating thumbnail for video: $videoPath", e)
            null
        } finally {
            try {
                retriever?.release()
            } catch (e: Exception) {
                Log.e(TAG, "Error releasing MediaMetadataRetriever", e)
            }
        }
    }
}