package com.example.app.repository

import android.util.Log
import com.example.app.api.RestaurantApiService
import com.example.app.model.ErrorResponse
import com.example.app.model.RestaurantInfo
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestaurantRepository {
    private val apiService: RestaurantApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(RestaurantApiService::class.java)
    }

    suspend fun getRestaurants(postcode: String): List<RestaurantInfo> {
        return try {
            Log.d("RestaurantSearch", "Fetching restaurants for: $postcode")
            apiService.getRestaurants(postcode)
        } catch (e: retrofit2.HttpException) {
            // Extract error body for HTTP errors
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("RestaurantSearch", "HTTP error: ${e.code()}, Body: $errorBody", e)

            try {
                // Try to parse as error message JSON
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                throw Exception(errorResponse.message)
            } catch (jsonEx: Exception) {
                // If parsing fails, just throw the original error message
                throw Exception(errorBody ?: e.message())
            }
        } catch (e: Exception) {
            Log.e("RestaurantSearch", "Network error: ${e.message}", e)
            throw e
        }
    }
}