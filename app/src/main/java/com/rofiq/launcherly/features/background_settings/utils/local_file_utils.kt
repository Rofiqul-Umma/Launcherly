package com.rofiq.launcherly.features.background_settings.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import java.io.File
import java.io.FileOutputStream

object LocalFileUtils {
    private const val TAG = "LocalFileUtils"

    /**
     * Copies a file from a content URI to the app's internal storage
     * @param context Application context
     * @param uri Content URI of the file to copy
     * @return Path to the copied file in internal storage, or null if failed
     */
    fun copyFileToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val contentResolver: ContentResolver = context.contentResolver
            val fileName = getFileName(contentResolver, uri) ?: "background_file"
            
            // Create directory for background files if it doesn't exist
            val backgroundDir = File(context.filesDir, "backgrounds")
            if (!backgroundDir.exists()) {
                backgroundDir.mkdirs()
            }
            
            // Create destination file
            val destFile = File(backgroundDir, fileName)
            
            // Copy file
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(destFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            
            destFile.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Error copying file to internal storage", e)
            null
        }
    }
    
    /**
     * Gets the file name from a content URI
     */
    private fun getFileName(contentResolver: ContentResolver, uri: Uri): String? {
        return try {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst()) {
                    cursor.getString(nameIndex)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting file name", e)
            null
        }
    }
    
    /**
     * Checks if a file path is a local file (not a URL)
     */
    fun isLocalFile(path: String): Boolean {
        return !path.startsWith("http://") && !path.startsWith("https://")
    }
    
    /**
     * Checks if a file path is an Android resource
     */
    fun isAndroidResource(path: String): Boolean {
        return path.startsWith("android.resource://")
    }
}