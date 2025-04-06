package com.example.app.model


data class RestaurantInfo(
    val name: String,
    val cuisines: List<String>,
    val rating: Double,
    val address: String
)
