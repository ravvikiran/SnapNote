package com.snapnote.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScreenshotProvider(private val context: Context) {

    suspend fun getRecentScreenshots(limit: Int = 50): List<Uri> = withContext(Dispatchers.IO) {
        val screenshots = mutableListOf<Uri>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED
        )

        // Filter for images containing "Screenshots" in their path
        val selection = "${MediaStore.Images.Media.DATA} LIKE ?"
        val selectionArgs = arrayOf("%Screenshots%")
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC LIMIT $limit"

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                screenshots.add(contentUri)
            }
        }
        return@withContext screenshots
    }
}
