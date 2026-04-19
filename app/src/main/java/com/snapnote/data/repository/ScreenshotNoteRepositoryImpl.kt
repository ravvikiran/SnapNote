package com.snapnote.data.repository

import android.util.Log
import com.snapnote.data.local.ScreenshotNoteDao
import com.snapnote.data.local.ScreenshotNoteEntity
import com.snapnote.domain.models.ScreenshotNote
import com.snapnote.domain.repository.ScreenshotNoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class ScreenshotNoteRepositoryImpl(
    private val dao: ScreenshotNoteDao
) : ScreenshotNoteRepository {

    override fun getAllNotes(): Flow<List<ScreenshotNote>> {
        return dao.getAllNotes()
            .map { entities ->
                entities.mapNotNull { entity ->
                    try {
                        entity.toDomain()
                    } catch (e: Exception) {
                        Log.e("ScreenshotNoteRepositoryImpl", "Error converting entity to domain", e)
                        null
                    }
                }
            }
            .flowOn(Dispatchers.IO)
            .catch { e ->
                Log.e("ScreenshotNoteRepositoryImpl", "Error in getAllNotes flow", e)
            }
    }

    override fun searchNotes(query: String): Flow<List<ScreenshotNote>> {
        return dao.searchNotes(query)
            .map { entities ->
                entities.mapNotNull { entity ->
                    try {
                        entity.toDomain()
                    } catch (e: Exception) {
                        Log.e("ScreenshotNoteRepositoryImpl", "Error converting entity to domain", e)
                        null
                    }
                }
            }
            .flowOn(Dispatchers.IO)
            .catch { e ->
                Log.e("ScreenshotNoteRepositoryImpl", "Error in searchNotes flow", e)
            }
    }

    override fun searchNotesByCategory(category: String): Flow<List<ScreenshotNote>> {
        return dao.searchNotesByCategory(category)
            .map { entities ->
                entities.mapNotNull { entity ->
                    try {
                        entity.toDomain()
                    } catch (e: Exception) {
                        Log.e("ScreenshotNoteRepositoryImpl", "Error converting entity to domain", e)
                        null
                    }
                }
            }
            .flowOn(Dispatchers.IO)
            .catch { e ->
                Log.e("ScreenshotNoteRepositoryImpl", "Error in searchNotesByCategory flow", e)
            }
    }

    override suspend fun insertNote(note: ScreenshotNote) {
        try {
            dao.insertNote(note.toEntity())
        } catch (e: Exception) {
            Log.e("ScreenshotNoteRepositoryImpl", "Error inserting note", e)
            throw e
        }
    }

    override suspend fun deleteNote(note: ScreenshotNote) {
        try {
            dao.deleteNote(note.toEntity())
        } catch (e: Exception) {
            Log.e("ScreenshotNoteRepositoryImpl", "Error deleting note", e)
            throw e
        }
    }

    override suspend fun getNoteByPath(path: String): ScreenshotNote? {
        return try {
            dao.getNoteByPath(path)?.toDomain()
        } catch (e: Exception) {
            Log.e("ScreenshotNoteRepositoryImpl", "Error getting note by path", e)
            null
        }
    }

    private fun ScreenshotNoteEntity.toDomain(): ScreenshotNote {
        return ScreenshotNote(
            id = id,
            imagePath = imagePath,
            extractedText = extractedText,
            tags = tags.split(",").filter { it.isNotBlank() }.map { it.trim() },
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
