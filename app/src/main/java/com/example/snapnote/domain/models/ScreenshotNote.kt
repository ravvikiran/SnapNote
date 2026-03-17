package com.example.snapnote.domain.models

data class ScreenshotNote(
    val id: String,
    val imageUri: String,
    val extractedText: String,
    val tags: List<String>,
    val category: String,
    val dateCreated: Long,
    val dateModified: Long
)
