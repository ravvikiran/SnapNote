package com.snapnote.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Migration
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ScreenshotNoteEntity::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun screenshotNoteDao(): ScreenshotNoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_screenshots_imagePath` ON `screenshots` (`imagePath`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_screenshots_category` ON `screenshots` (`category`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_screenshots_dateAdded` ON `screenshots` (`dateAdded`)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "snapnote-db"
                )
                    .enableMultiInstanceInvalidation()
                    .setJournalMode(RoomDatabase.JournalMode.AUTOMATIC)
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
