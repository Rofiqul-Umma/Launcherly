package com.rofiq.launcherly.utils

import android.net.Uri
import android.util.Log
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import androidx.core.net.toUri

object GoogleDriveUtils {
    private const val TAG = "GoogleDriveUtils"
    
    /**
     * Converts a Google Drive sharing URL to a direct download URL.
     * 
     * @param sharingUrl The Google Drive sharing URL
     * @return The direct download URL, or the original URL if it's not a Google Drive URL
     */
    fun convertSharingUrlToDownloadUrl(sharingUrl: String): String {
        // Check if it's a Google Drive sharing URL
        if (!sharingUrl.contains("drive.google.com")) {
            return sharingUrl
        }
        
        try {
            // Parse the URL to extract query parameters
            val uri = sharingUrl.toUri()
            
            // Extract the file ID from different possible formats
            val fileId = when {
                sharingUrl.contains("/file/d/") -> {
                    // Format: https://drive.google.com/file/d/FILE_ID/view?usp=sharing
                    val regex = """/file/d/([^/]+)/""".toRegex()
                    regex.find(sharingUrl)?.groupValues?.get(1)
                }
                sharingUrl.contains("open?id=") -> {
                    // Format: https://drive.google.com/open?id=FILE_ID
                    uri.getQueryParameter("id")
                }
                uri.getQueryParameter("id") != null -> {
                    // Any URL with an 'id' parameter
                    uri.getQueryParameter("id")
                }
                else -> {
                    // Try to extract from path
                    val pathSegments = uri.pathSegments
                    val fileIndex = pathSegments.indexOf("file")
                    if (fileIndex != -1 && pathSegments.size > fileIndex + 2 && pathSegments[fileIndex + 1] == "d") {
                        pathSegments[fileIndex + 2]
                    } else {
                        null
                    }
                }
            }
            
            // Return the direct download URL if we found a file ID
            return if (fileId != null) {
                val decodedFileId = URLDecoder.decode(fileId, StandardCharsets.UTF_8.toString())
                Log.d(TAG, "Converted Google Drive URL: $sharingUrl -> https://drive.google.com/uc?export=view&id=$decodedFileId")
                "https://drive.google.com/uc?export=view&id=$decodedFileId"
            } else {
                Log.w(TAG, "Could not extract file ID from Google Drive URL: $sharingUrl")
                sharingUrl
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing Google Drive URL: $sharingUrl", e)
            return sharingUrl
        }
    }
}