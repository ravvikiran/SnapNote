package com.snapnote.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ScreenshotNoteEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun screenshotNoteDao(): ScreenshotNoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "snapnote-db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
