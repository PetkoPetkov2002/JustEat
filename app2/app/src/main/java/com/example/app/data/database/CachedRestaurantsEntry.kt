package com.example.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single entry in the `cached_restaurants` table.
 * Stores the results of a restaurant search for a specific postcode.
 *
 * @property postcode The postcode searched (primary key).
 * @property restaurantsJson A JSON string representation of the list of [RestaurantInfo] objects.
 * @property timestamp The time when this cache entry was created (in milliseconds since epoch).
 */
@Entity(tableName = "cached_restaurants")
data class CachedRestaurantsEntry(
    @PrimaryKey val postcode: String,
    val restaurantsJson: String, // Store list as JSON
    val timestamp: Long = System.currentTimeMillis()
)