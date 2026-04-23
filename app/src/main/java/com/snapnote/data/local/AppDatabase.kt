package com.snapnote.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.room.util.SchemaMigrationUtil
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ScreenshotNoteEntity::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun screenshotNoteDao(): ScreenshotNoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration from version 1 to 2 adds indices on imagePath (unique), category, and dateAdded
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add unique index on imagePath
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_screenshots_imagePath` ON `screenshots` (`imagePath`)")
                // Add index on category for faster filtering
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_screenshots_category` ON `screenshots` (`category`)")
                // Add index on dateAdded for faster ordering
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
