package com.example.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_restaurants")
data class CachedRestaurantsEntry(
    @PrimaryKey val postcode: String,
    val restaurantsJson: String, // Store list as JSON
    val timestamp: Long = System.currentTimeMillis()
)