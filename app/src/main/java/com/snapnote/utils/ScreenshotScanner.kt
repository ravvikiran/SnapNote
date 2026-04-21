package com.snapnote.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScreenshotScanner(private val context: Context) {

    suspend fun getRecentScreenshots(limit: Int = 50): List<Uri> = withContext(Dispatchers.IO) {
        val screenshots = mutableListOf<Uri>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_ADDED
        )

        try {
            val cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Use proper scoped storage API for Android 10+
                val bundle = android.os.Bundle().apply {
                    putString(
                        android.content.ContentResolver.QUERY_ARG_SQL_SELECTION,
                        "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
                    )
                    putStringArray(
                        android.content.ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS,
                        arrayOf("Screenshots")
                    )
                    putString(
                        android.content.ContentResolver.QUERY_ARG_SQL_SORT_ORDER,
                        "${MediaStore.Images.Media.DATE_ADDED} DESC"
                    )
                    putInt(android.content.ContentResolver.QUERY_ARG_LIMIT, limit)
                }
                context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    bundle,
                    null
                )
            } else {
                // For API 29 and below, use the legacy method
                @Suppress("DEPRECATION")
                val selection = "${MediaStore.Images.Media.DATA} LIKE ?"
                val selectionArgs = arrayOf("%Screenshots%")
                val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
                context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
                )
            }

            cursor?.use { c ->
                val idColumn = c.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val count = c.count
                if (count > limit) {
                    // Handle limit manually for API < 29
                    var processed = 0
                    while (c.moveToNext() && processed < limit) {
                        val id = c.getLong(idColumn)
                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        screenshots.add(contentUri)
                        processed++
                    }
                } else {
                    while (c.moveToNext()) {
                        val id = c.getLong(idColumn)
                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        screenshots.add(contentUri)
                    }
                }
            } ?: Log.w("ScreenshotScanner", "Query returned null cursor")
        } catch (e: Exception) {
            Log.e("ScreenshotScanner", "Error querying screenshots: ${e.message}", e)
        }

        return@withContext screenshots
    }
}
