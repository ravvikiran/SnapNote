package com.example.snapnote.data.repository

import com.example.snapnote.data.local.ScreenshotNoteDao
import com.example.snapnote.data.local.ScreenshotNoteEntity
import com.example.snapnote.domain.models.ScreenshotNote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScreenshotNoteRepositoryImpl(
    private val dao: ScreenshotNoteDao
) {

    fun getAllNotes(): Flow<List<ScreenshotNote>> {
        return dao.getAllNotes().map { entities -> entities.map { it.toDomainModel() } }
    }

    suspend fun getNoteById(id: Int): ScreenshotNote? {
        return dao.getNoteById(id)?.toDomainModel()
    }

    fun searchNotes(query: String): Flow<List<ScreenshotNote>> {
        return dao.searchNotes(query).map { entities -> entities.map { it.toDomainModel() } }
    }

    suspend fun saveNote(note: ScreenshotNote) {
        dao.insert(note.toEntity())
    }

    suspend fun deleteNote(note: ScreenshotNote) {
        dao.delete(note.toEntity())
    }

    // Mapper Extension Functions
    private fun ScreenshotNoteEntity.toDomainModel() = ScreenshotNote(
        id = id,
        imagePath = imagePath,
        extractedText = extractedText,
        tags = tags,
        category = category,
        dateCreated = dateCreated
    )

    private fun ScreenshotNote.toEntity() = ScreenshotNoteEntity(
        id = id,
        imagePath = imagePath,
        extractedText = extractedText,
        tags = tags,
        category = category,
        dateCreated = dateCreated
    )
}
