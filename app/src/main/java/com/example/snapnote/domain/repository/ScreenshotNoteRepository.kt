package com.snapnote.domain.repository

import com.snapnote.domain.models.ScreenshotNote
import kotlinx.coroutines.flow.Flow

interface ScreenshotNoteRepository {
    fun getAllNotes(): Flow<List<ScreenshotNote>>
    fun searchNotes(query: String): Flow<List<ScreenshotNote>>
    suspend fun insertNote(note: ScreenshotNote)
    suspend fun deleteNote(note: ScreenshotNote)
    suspend fun getNoteByPath(path: String): ScreenshotNote?
}
