
package com.example.app.api

import com.example.app.model.RestaurantInfo
import retrofit2.http.GET
import retrofit2.http.Path

interface RestaurantApiService {
    @GET("restaurants/{postcode}")
    suspend fun getRestaurants(@Path("postcode") postcode: String): List<RestaurantInfo>
}
