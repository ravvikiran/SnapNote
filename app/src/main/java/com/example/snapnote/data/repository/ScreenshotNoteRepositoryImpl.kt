package com.example.snapnote.data.repository

import com.example.snapnote.data.local.ScreenshotNoteDao
import com.example.snapnote.data.local.ScreenshotNoteEntity
import com.example.snapnote.domain.models.ScreenshotNote
import com.example.snapnote.domain.repository.ScreenshotNoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScreenshotNoteRepositoryImpl(
    private val dao: ScreenshotNoteDao
) : ScreenshotNoteRepository {

    override fun getAllNotes(): Flow<List<ScreenshotNote>> {
        return dao.getAllNotes().map { entities -> entities.map { it.toDomainModel() } }
    }

    override suspend fun getNoteById(id: String): ScreenshotNote? {
        return dao.getNoteById(id)?.toDomainModel()
    }

    override fun searchNotes(query: String): Flow<List<ScreenshotNote>> {
        return dao.searchNotes(query).map { entities -> entities.map { it.toDomainModel() } }
    }

    override suspend fun saveNote(note: ScreenshotNote) {
        dao.insert(note.toEntity())
    }

    override suspend fun deleteNote(note: ScreenshotNote) {
        dao.delete(note.toEntity())
    }

    // Mapper Extension Functions
    private fun ScreenshotNoteEntity.toDomainModel() = ScreenshotNote(
        id = id,
        imageUri = imageUri,
        extractedText = extractedText,
        tags = if (tags.isEmpty()) emptyList() else tags.split(","),
        category = category,
        dateCreated = dateCreated,
        dateModified = dateModified
    )

    private fun ScreenshotNote.toEntity() = ScreenshotNoteEntity(
        id = id,
        imageUri = imageUri,
        extractedText = extractedText,
        tags = tags.joinToString(","),
        category = category,
        dateCreated = dateCreated,
        dateModified = dateModified
    )
}
