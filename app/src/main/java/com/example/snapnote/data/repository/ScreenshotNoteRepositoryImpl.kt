package com.snapnote.data.repository

import com.snapnote.data.local.ScreenshotNoteDao
import com.snapnote.data.local.ScreenshotNoteEntity
import com.snapnote.domain.models.ScreenshotNote
import com.snapnote.domain.repository.ScreenshotNoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScreenshotNoteRepositoryImpl(
    private val dao: ScreenshotNoteDao
) : ScreenshotNoteRepository {

    override fun getAllNotes(): Flow<List<ScreenshotNote>> {
        return dao.getAllNotes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchNotes(query: String): Flow<List<ScreenshotNote>> {
        return dao.searchNotes(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertNote(note: ScreenshotNote) {
        dao.insertNote(note.toEntity())
    }

    override suspend fun deleteNote(note: ScreenshotNote) {
        dao.deleteNote(note.toEntity())
    }

    override suspend fun getNoteByPath(path: String): ScreenshotNote? {
        return dao.getNoteByPath(path)?.toDomain()
    }

    private fun ScreenshotNoteEntity.toDomain(): ScreenshotNote {
        return ScreenshotNote(
            id = id,
            imagePath = imagePath,
            extractedText = extractedText,
            tags = tags.split(",").filter { it.isNotBlank() },
            category = category,
            dateAdded = dateAdded
        )
    }

    private fun ScreenshotNote.toEntity(): ScreenshotNoteEntity {
        return ScreenshotNoteEntity(
            id = id,
            imagePath = imagePath,
            extractedText = extractedText,
            tags = tags.joinToString(","),
            category = category,
            dateAdded = dateAdded
        )
    }
}
