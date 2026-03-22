package com.example.snapnote.utils

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore

data class LocalScreenshot(
    val id: Long,
    val uri: String,
    val dateAdded: Long
)

class ScreenshotProvider(private val context: Context) {
    fun fetchScreenshots(): List<LocalScreenshot> {
        val screenshots = mutableListOf<LocalScreenshot>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATA
        )
        
        // Filter for Screenshots folder
        val selection = "${MediaStore.Images.Media.DATA} LIKE ?"
        val selectionArgs = arrayOf("%/Screenshots/%")
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val dateAdded = cursor.getLong(dateColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                screenshots.add(LocalScreenshot(id, contentUri.toString(), dateAdded))
            }
        }
        return screenshots
    }
}
