package com.snapnote.util

import com.snapnote.data.local.ScreenshotNoteEntity
import com.snapnote.domain.models.ScreenshotNote

object ScreenshotNoteMapper {
    fun entityToDomain(entity: ScreenshotNoteEntity): ScreenshotNote {
        return ScreenshotNote(
            id = entity.id,
            imagePath = entity.imagePath,
            extractedText = entity.extractedText,
            tags = parseTags(entity.tags),
            category = entity.category,
            dateAdded = entity.dateAdded
        )
    }

    fun domainToEntity(domain: ScreenshotNote): ScreenshotNoteEntity {
        return ScreenshotNoteEntity(
            id = domain.id,
            imagePath = domain.imagePath,
            extractedText = domain.extractedText,
            tags = domain.tags.joinToString(","),
            category = domain.category,
            dateAdded = domain.dateAdded
        )
    }

    private fun parseTags(tagsString: String): List<String> {
        return tagsString.split(",")
            .filter { it.isNotBlank() }
            .map { it.trim() }
            .toList()
    }
}
