package com.snapnote.data.repository

import android.util.Log
import com.snapnote.data.local.ScreenshotNoteDao
import com.snapnote.domain.models.ScreenshotNote
import com.snapnote.domain.repository.ScreenshotNoteRepository
import com.snapnote.util.ScreenshotNoteMapper
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
                        ScreenshotNoteMapper.entityToDomain(entity)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error converting entity to domain", e)
                        null
                    }
                }
            }
            .flowOn(Dispatchers.IO)
            .catch { e ->
                Log.e(TAG, "Error in getAllNotes flow", e)
                emit(emptyList())
            }
    }

    override fun searchNotes(query: String): Flow<List<ScreenshotNote>> {
        return dao.searchNotes(query)
            .map { entities ->
                entities.mapNotNull { entity ->
                    try {
                        ScreenshotNoteMapper.entityToDomain(entity)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error converting entity to domain", e)
                        null
                    }
                }
            }
            .flowOn(Dispatchers.IO)
            .catch { e ->
                Log.e(TAG, "Error in searchNotes flow", e)
                emit(emptyList())
            }
    }

    override fun searchNotesByCategory(category: String): Flow<List<ScreenshotNote>> {
        return dao.searchNotesByCategory(category)
            .map { entities ->
                entities.mapNotNull { entity ->
                    try {
                        ScreenshotNoteMapper.entityToDomain(entity)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error converting entity to domain", e)
                        null
                    }
                }
            }
            .flowOn(Dispatchers.IO)
            .catch { e ->
                Log.e(TAG, "Error in searchNotesByCategory flow", e)
                emit(emptyList())
            }
    }

    override suspend fun insertNote(note: ScreenshotNote) {
        try {
            dao.insertNote(ScreenshotNoteMapper.domainToEntity(note))
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting note", e)
            throw e
        }
    }

    override suspend fun deleteNote(note: ScreenshotNote) {
        try {
            dao.deleteNote(ScreenshotNoteMapper.domainToEntity(note))
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting note", e)
            throw e
        }
    }

    override suspend fun getNoteByPath(path: String): ScreenshotNote? {
        return try {
            dao.getNoteByPath(path)?.let { ScreenshotNoteMapper.entityToDomain(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting note by path", e)
            null
        }
    }

    companion object {
        private const val TAG = "ScreenshotNoteRepo"
    }
}
