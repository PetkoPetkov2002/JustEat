package com.example.app.model

/**
 * Data class representing the information for a single restaurant.
 *
 * @property name The name of the restaurant.
 * @property cuisines A list of cuisine types offered by the restaurant.
 * @property rating The average customer rating of the restaurant.
 * @property address The full address of the restaurant.
 */
data class RestaurantInfo(
    val name: String,
    val cuisines: List<String>,
    val rating: Double,
    val address: String
)
