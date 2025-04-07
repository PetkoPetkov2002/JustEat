package com.example.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object (DAO) for interacting with the `cached_restaurants` table.
 * Defines methods for querying, inserting, and deleting cached restaurant data.
 */
@Dao
interface CacheDao {
    /**
     * Retrieves a cached entry for a given postcode if it exists and is not stale.
     *
     * @param postcode The postcode key for the cache entry.
     * @param cutoffTime The timestamp defining the maximum age for a valid cache entry.
     * @return The [CachedRestaurantsEntry] if found and valid, otherwise null.
     */
    @Query("SELECT * FROM cached_restaurants WHERE postcode = :postcode AND timestamp > :cutoffTime")
    suspend fun getValidCache(postcode: String, cutoffTime: Long): CachedRestaurantsEntry?

    /**
     * Inserts or replaces a cache entry.
     * If an entry with the same postcode already exists, it will be replaced.
     *
     * @param entry The [CachedRestaurantsEntry] to insert or update.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun cacheResults(entry: CachedRestaurantsEntry)

    /**
     * Deletes all cache entries older than the specified cutoff time.
     *
     * @param cutoffTime The timestamp defining the maximum age for a cache entry to be kept.
     */
    @Query("DELETE FROM cached_restaurants WHERE timestamp <= :cutoffTime")
    suspend fun clearStaleCache(cutoffTime: Long)
}