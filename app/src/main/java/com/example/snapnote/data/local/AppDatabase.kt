package com.snapnote.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ScreenshotNoteEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun screenshotNoteDao(): ScreenshotNoteDao
}
