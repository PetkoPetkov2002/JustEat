package com.example.service

import com.example.model.JustEatResponse
import com.example.model.RestaurantInfo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class JustEatService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    suspend fun getRestaurantsByPostcode(postcode: String): List<RestaurantInfo> {
        val response: JustEatResponse = client.get("https://uk.api.just-eat.io/discovery/uk/restaurants/enriched/bypostcode/$postcode") {
            headers {
                append("Accept", "application/json")
            }
        }.body()

        // Extract the first 10 restaurants or all if less than 10
        return response.restaurants
            .take(10)
            .map { restaurant ->
                RestaurantInfo(
                    name = restaurant.name,
                    cuisines = restaurant.cuisines.map { it.name },
                    rating = restaurant.rating.starRating,
                    address = "${restaurant.address.firstLine}, ${restaurant.address.city}, ${restaurant.address.postalCode}"
                )
            }
    }
} 