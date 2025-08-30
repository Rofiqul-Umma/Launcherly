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
            // Extract the file ID from different possible formats
            val fileId = extractFileIdFromDriveUrl(sharingUrl)
            
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
    
    /**
     * Extracts file ID from Google Drive URL
     */
    fun extractFileIdFromDriveUrl(url: String): String? {
        return try {
            // Handle different Google Drive URL formats
            when {
                url.contains("/file/d/") -> {
                    // Format: https://drive.google.com/file/d/FILE_ID/view?usp=sharing
                    val regex = """/file/d/([^/]+)/""".toRegex()
                    regex.find(url)?.groupValues?.get(1)
                }
                url.contains("open?id=") -> {
                    // Format: https://drive.google.com/open?id=FILE_ID
                    val uri = url.toUri()
                    uri.getQueryParameter("id")
                }
                else -> {
                    // Try general pattern matching
                    val regex = """(?:/file/d/|id=)([a-zA-Z0-9_-]+)""".toRegex()
                    regex.find(url)?.groupValues?.get(1)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting file ID from URL: $url", e)
            null
        }
    }
    
    /**
     * Creates a thumbnail URL for a Google Drive file
     */
    fun createThumbnailUrl(sharingUrl: String): String? {
        return try {
            val fileId = extractFileIdFromDriveUrl(sharingUrl)
            if (fileId != null) {
                val decodedFileId = URLDecoder.decode(fileId, StandardCharsets.UTF_8.toString())
                "https://drive.google.com/thumbnail?id=$decodedFileId&sz=s500"
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating thumbnail URL for: $sharingUrl", e)
            null
        }
    }
}