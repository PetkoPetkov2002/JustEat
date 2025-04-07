package com.example.app.api

import com.example.app.model.RestaurantInfo
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit service interface defining the API endpoints for fetching restaurant data.
 */
interface RestaurantApiService {
    /**
     * Fetches a list of restaurants for a given postcode.
     *
     * @param postcode The UK postcode to search for restaurants.
     * @return A list of [RestaurantInfo] objects.
     */
    @GET("restaurants/{postcode}")
    suspend fun getRestaurants(@Path("postcode") postcode: String): List<RestaurantInfo>
}
