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

class RestaurantRepository(private val context: Context) {
    // Cache validity - 15 minutes
    private val CACHE_DURATION_MS = 15 * 60 * 1000L
    
    // Base URL - consider moving to a config file in a real app
    private val BASE_URL = "http://10.0.2.2:8080/" // Emulator
    // private val BASE_URL = "http://192.168.1.X:8080/" // Physical device - replace X with your IP

    // Get database instance using the singleton pattern
    private val cacheDao = AppDatabase.getDatabase(context).cacheDao()

    // Create API service directly
    private val apiService: RestaurantApiService = createApiService()
    
    private fun createApiService(): RestaurantApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RestaurantApiService::class.java)
    }

    private val gson = Gson()

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

    private fun deserializeRestaurants(json: String): List<RestaurantInfo> {
        val type = object : TypeToken<List<RestaurantInfo>>() {}.type
        return gson.fromJson(json, type)
    }
}