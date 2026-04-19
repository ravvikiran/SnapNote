package com.snapnote.data.local

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ScreenshotNoteEntity::class], version = 1, exportSchema = true)
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
                )
                    .enableMultiInstanceInvalidation()
                    .setJournalMode(RoomDatabase.JournalMode.AUTOMATIC)  // Enables WAL mode automatically
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.d("AppDatabase", "Database created with WAL mode")
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            Log.d("AppDatabase", "Database opened")
                        }
                    })
                    // For v1.0, we use fallbackToDestructiveMigration
                    // TODO: Implement proper migrations for schema changes in future versions
                    // using Migration objects to preserve user data during updates
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
