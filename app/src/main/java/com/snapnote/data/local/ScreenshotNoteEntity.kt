package com.snapnote.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "screenshots",
    indices = [
        Index(value = ["imagePath"], unique = true),
        Index(value = ["category"]),
        Index(value = ["dateAdded"])
    ]
)
data class ScreenshotNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imagePath: String,
    val extractedText: String,
    val tags: String,
    val category: String,
    val dateAdded: Long = System.currentTimeMillis()
)
