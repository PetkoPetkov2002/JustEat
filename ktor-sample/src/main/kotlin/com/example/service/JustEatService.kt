package com.example.service

import CacheService
import com.example.model.JustEatResponse
import com.example.model.RestaurantInfo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.headers
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
/**
 * Service class responsible for fetching restaurant data from the Just Eat API,
 * processing it into the required format, and managing the cache.
 */
class JustEatService {
    /**
     * Ktor HTTP client configured for making requests to the Just Eat API.
     * Uses CIO engine and Kotlinx Serialization for JSON content negotiation.
     */
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }
    // Service to handle caching of restaurant data to reduce API calls.
    private val cacheService = CacheService()
    /**
     * Fetches restaurant information for a given UK postcode.
     * 1. Checks the cache for existing data.
     * 2. If cache miss, calls the Just Eat API.
     * 3. Processes the API response, extracting required fields and limiting to 10 results.
     * 4. Stores the fresh results in the cache.
     * 5. Returns the list of [RestaurantInfo] objects.
     *
     * @param postcode The UK postcode to search for.
     * @return A list of [RestaurantInfo] objects (up to 10).
     */
    suspend fun getRestaurantsByPostcode(postcode: String): List<RestaurantInfo> {
        //First check if restaurant is in cache
        val cachedRestaurants = cacheService.get(postcode)
        if(cachedRestaurants != null) {
            return cachedRestaurants
        }
        //if the restaurant doesn't exist in the cache then initiate API call
        val response: JustEatResponse = client.get("https://uk.api.just-eat.io/discovery/uk/restaurants/enriched/bypostcode/$postcode") {
            headers {
                append("Accept", "application/json")
            }
        }.body()

        // Extract the first 10 restaurants or all if less than 10
        val restaurants =  response.restaurants
            .take(10)
            .map { restaurant ->
                RestaurantInfo(
                    name = restaurant.name,
                    cuisines = restaurant.cuisines.map { it.name },
                    rating = restaurant.rating.starRating,
                    address = "${restaurant.address.firstLine}, ${restaurant.address.city}, ${restaurant.address.postalCode}"
                )
            }
        //Add restaurant entry to the cache
        cacheService.set(postcode, restaurants)
        return restaurants
    }
} 