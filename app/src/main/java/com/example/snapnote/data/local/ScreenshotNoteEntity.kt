package com.example.snapnote.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "screenshot_notes")
data class ScreenshotNoteEntity(
    @PrimaryKey val id: String,
    val imageUri: String,
    val extractedText: String,
    val tags: String, // Comma-separated tags (e.g. "java,programming")
    val category: String,
    val dateCreated: Long,
    val dateModified: Long
)
