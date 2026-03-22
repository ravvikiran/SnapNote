package com.example.snapnote.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScreenshotNoteDao {
    @Query("SELECT * FROM screenshot_notes ORDER BY dateCreated DESC")
    fun getAllNotes(): Flow<List<ScreenshotNoteEntity>>

    @Query("SELECT * FROM screenshot_notes WHERE id = :id")
    suspend fun getNoteById(id: Int): ScreenshotNoteEntity?

    @Query("SELECT * FROM screenshot_notes WHERE category = :category ORDER BY dateCreated DESC")
    fun getNotesByCategory(category: String): Flow<List<ScreenshotNoteEntity>>

    @Query("SELECT * FROM screenshot_notes WHERE extractedText LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY dateCreated DESC")
    fun searchNotes(query: String): Flow<List<ScreenshotNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: ScreenshotNoteEntity)

    @Update
    suspend fun update(note: ScreenshotNoteEntity)

    @Delete
    suspend fun delete(note: ScreenshotNoteEntity)
}
