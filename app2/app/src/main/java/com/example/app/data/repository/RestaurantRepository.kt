package com.example.app.data.repository

import android.content.Context
import android.util.Log
import com.example.app.api.RestaurantApiService
import com.example.app.data.database.AppDatabase
import com.example.app.data.database.CachedRestaurantsEntry
import com.example.app.model.ErrorResponse
import com.example.app.model.RestaurantInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

/**
 * Repository class responsible for fetching restaurant data.
 * It acts as a single source of truth for restaurant data, handling both
 * network requests (via [RestaurantApiService]) and local caching (via [CacheDao]).
 *
 * @param context Application context, needed for initializing the database.
 */
class RestaurantRepository(private val context: Context) {
    // Defines the maximum age for cached data before it's considered stale.
    private val CACHE_DURATION_MS = 15 * 60 * 1000L // 15 minutes
    
    // Base URL for the backend API.
    // NOTE: 10.0.2.2 is the special alias for the host machine's localhost from the Android emulator.
    // Use your machine's network IP if testing on a physical device connected to the same Wi-Fi.
    private val BASE_URL = "http://10.0.2.2:8080/"
    // private val BASE_URL = "http://192.168.1.X:8080/" // Example for physical device

    // Lazily initializes the DAO from the singleton AppDatabase instance.
    private val cacheDao = AppDatabase.getDatabase(context).cacheDao()

    // Creates the Retrofit API service instance.
    private val apiService: RestaurantApiService = createApiService()
    
    /**
     * Creates and configures the Retrofit service instance.
     */
    private fun createApiService(): RestaurantApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RestaurantApiService::class.java)
    }

    // Gson instance for JSON serialization/deserialization of cache data.
    private val gson = Gson()

    /**
     * Fetches restaurants for the given postcode.
     * Tries to retrieve from cache first. If cache is missing or stale,
     * fetches from the network API, caches the result, and returns it.
     * Handles potential network and HTTP errors.
     *
     * @param postcode The postcode to search for.
     * @return A list of [RestaurantInfo].
     * @throws Exception if fetching fails or an error occurs.
     */
    suspend fun getRestaurants(postcode: String): List<RestaurantInfo> {
        Log.d("RestaurantRepository", "Fetching restaurants for $postcode")
        
        // Make sure we run database operations on IO dispatcher
        return withContext(Dispatchers.IO) {
            try {
                // Calculate cutoff time for cache validity
                val cutoffTime = System.currentTimeMillis() - CACHE_DURATION_MS

                // Try to get valid cache
                val cacheEntry = cacheDao.getValidCache(postcode, cutoffTime)

                // Return cache if valid
                if (cacheEntry != null) {
                    Log.d("RestaurantRepository", "Using cached data for $postcode")
                    return@withContext deserializeRestaurants(cacheEntry.restaurantsJson)
                }

                // Otherwise fetch from network
                Log.d("RestaurantRepository", "Cache miss, fetching from network for $postcode")
                
                val restaurants = apiService.getRestaurants(postcode)

                // Cache the new results
                val restaurantsJson = gson.toJson(restaurants)
                val entry = CachedRestaurantsEntry(
                    postcode = postcode,
                    restaurantsJson = restaurantsJson
                )
                cacheDao.cacheResults(entry)

                // Clean up old entries periodically
                cacheDao.clearStaleCache(cutoffTime)

                restaurants
            } catch (e: retrofit2.HttpException) {
                // Extract error body for HTTP errors
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("RestaurantRepository", "HTTP error: ${e.code()}, Body: $errorBody", e)

                try {
                    // Try to parse as error message JSON
                    val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                    throw Exception(errorResponse.message)
                } catch (jsonEx: Exception) {
                    // If parsing fails, just throw the original error message
                    throw Exception(errorBody ?: e.message())
                }
            } catch (e: IOException) {
                Log.e("RestaurantRepository", "Network error: ${e.message}", e)
                throw Exception("Network error: Please check your internet connection")
            } catch (e: Exception) {
                Log.e("RestaurantRepository", "Unexpected error: ${e.message}", e)
                throw e
            }
        }
    }

    /**
     * Deserializes a JSON string into a list of [RestaurantInfo] objects.
     *
     * @param json The JSON string representing the list of restaurants.
     * @return The deserialized list of [RestaurantInfo].
     */
    private fun deserializeRestaurants(json: String): List<RestaurantInfo> {
        val type = object : TypeToken<List<RestaurantInfo>>() {}.type
        return gson.fromJson(json, type)
    }
}