package com.snapnote.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScreenshotNoteDao {
    @Query("SELECT * FROM screenshots ORDER BY dateAdded DESC")
    fun getAllNotes(): Flow<List<ScreenshotNoteEntity>>

    @Query("SELECT * FROM screenshots WHERE extractedText LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    fun searchNotes(query: String): Flow<List<ScreenshotNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: ScreenshotNoteEntity)

    @Delete
    suspend fun deleteNote(note: ScreenshotNoteEntity)
    
    @Query("SELECT * FROM screenshots WHERE imagePath = :path LIMIT 1")
    suspend fun getNoteByPath(path: String): ScreenshotNoteEntity?
}
