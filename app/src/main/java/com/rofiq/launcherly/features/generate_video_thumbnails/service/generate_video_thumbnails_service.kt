package com.rofiq.launcherly.features.generate_video_thumbnails.service

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GenerateVideoThumbnailsService @Inject constructor(val context: Context) {

    companion object {
        private const val THUMBNAIL_SIZE = 256
    }

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
                // Decode a downscaled frame where supported so we never allocate a
                // full-resolution bitmap for a thumbnail.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    retriever.getScaledFrameAtTime(
                        5000L,
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
                        THUMBNAIL_SIZE,
                        THUMBNAIL_SIZE
                    )
                } else {
                    retriever.getFrameAtTime(5000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                }
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