package com.snapnote.domain.models

data class ScreenshotNote(
    val id: Int = 0,
    val imagePath: String,
    val extractedText: String,
    val tags: List<String>,
    val category: String,
    val dateAdded: Long
)
