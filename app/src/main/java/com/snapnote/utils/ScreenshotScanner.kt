package com.snapnote.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScreenshotScanner(private val context: Context) {

    suspend fun getRecentScreenshots(limit: Int = 50): List<Uri> = withContext(Dispatchers.IO) {
        val screenshots = mutableListOf<Uri>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_ADDED
        )

        val cursor = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val bundle = android.os.Bundle().apply {
                putString(android.content.ContentResolver.QUERY_ARG_SQL_SELECTION, "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?")
                putStringArray(android.content.ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, arrayOf("Screenshots"))
                putString(android.content.ContentResolver.QUERY_ARG_SQL_SORT_ORDER, "${MediaStore.Images.Media.DATE_ADDED} DESC")
                putInt(android.content.ContentResolver.QUERY_ARG_LIMIT, limit)
            }
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                bundle,
                null
            )
        } else {
            @Suppress("DEPRECATION")
            val selection = "${MediaStore.Images.Media.DATA} LIKE ?"
            val selectionArgs = arrayOf("%Screenshots%")
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC LIMIT $limit"
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
            while (c.moveToNext()) {
                val id = c.getLong(idColumn)
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
