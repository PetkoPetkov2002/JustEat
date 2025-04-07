package com.example.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database class for the application.
 * Defines the database configuration and serves as the main access point
 * for the underlying connection to the app's persisted, structured data.
 *
 * Uses a singleton pattern to ensure only one instance of the database exists.
 */
@Database(entities = [CachedRestaurantsEntry::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Provides access to the Data Access Object (DAO) for cache operations.
     */
    abstract fun cacheDao(): CacheDao

    companion object {
        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "restaurant_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}