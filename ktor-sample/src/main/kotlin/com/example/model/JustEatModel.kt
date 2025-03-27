package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class JustEatResponse(
    val restaurants: List<Restaurant> = emptyList()
)

@Serializable
data class Restaurant(
    val id: String,
    val name: String,
    val cuisines: List<Cuisine>,
    val rating: Rating,
    val address: Address
)

@Serializable
data class Cuisine(
    val name: String,
    val uniqueName: String
)

@Serializable
data class Rating(
    val starRating: Double,
    val count: Int
)

@Serializable
data class Address(
    val firstLine: String,
    val city: String,
    val postalCode: String
)

@Serializable
data class RestaurantInfo(
    val name: String,
    val cuisines: List<String>,
    val rating: Double,
    val address: String
) 