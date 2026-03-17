package com.example.snapnote.domain.repository

import com.example.snapnote.domain.models.ScreenshotNote
import kotlinx.coroutines.flow.Flow

interface ScreenshotNoteRepository {
    fun getAllNotes(): Flow<List<ScreenshotNote>>
    suspend fun getNoteById(id: String): ScreenshotNote?
    fun searchNotes(query: String): Flow<List<ScreenshotNote>>
    suspend fun saveNote(note: ScreenshotNote)
    suspend fun deleteNote(note: ScreenshotNote)
}
