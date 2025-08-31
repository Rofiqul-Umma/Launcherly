package com.rofiq.launcherly.features.generate_video_thumbnails.service

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GenerateVideoThumbnailsService @Inject constructor(val context: Context) {
    suspend fun generateThumbnail(videoPath: String): Bitmap? {
        var retriever: MediaMetadataRetriever? = null
        return withContext(Dispatchers.IO){
            try {
                if (videoPath.contains("drive.google.com")) {
                    return@withContext null
                }
                val videoUri = videoPath.toUri()
                retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, videoUri)
                val thumbnail =
                    retriever.getFrameAtTime(5000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                thumbnail
            } catch (e: Exception) {
                null
            } finally {
                try {
                    retriever?.release()
                } catch (e: Exception) {
                }
            }
        }
    }
}