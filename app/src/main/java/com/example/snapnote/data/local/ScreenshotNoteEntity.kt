package com.example.snapnote.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "screenshot_notes")
data class ScreenshotNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imagePath: String,
    val extractedText: String,
    val tags: String,
    val category: String,
    val dateCreated: Long
)
