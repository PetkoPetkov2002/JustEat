package com.example.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CacheDao {
    @Query("SELECT * FROM cached_restaurants WHERE postcode = :postcode AND timestamp > :cutoffTime")
    suspend fun getValidCache(postcode: String, cutoffTime: Long): CachedRestaurantsEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun cacheResults(entry: CachedRestaurantsEntry)

    @Query("DELETE FROM cached_restaurants WHERE timestamp <= :cutoffTime")
    suspend fun clearStaleCache(cutoffTime: Long)
}