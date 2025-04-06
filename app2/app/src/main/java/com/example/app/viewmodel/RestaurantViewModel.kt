package com.example.app.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.RestaurantInfo
import com.example.app.data.repository.RestaurantRepository
import kotlinx.coroutines.launch

class RestaurantViewModel(application: Application) : AndroidViewModel(application) {
    val searchQuery = mutableStateOf("")
    val restaurants = mutableStateListOf<RestaurantInfo>()
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    private val repository = RestaurantRepository(application)

    fun searchRestaurants(query: String) {
        // Clear previous results and errors
        errorMessage.value = null
        isLoading.value = true
        restaurants.clear()

        viewModelScope.launch {
            try {
                val fetchedRestaurants = repository.getRestaurants(query)
                restaurants.addAll(fetchedRestaurants)
            } catch (e: Exception) {
                errorMessage.value = "Failed to fetch restaurants: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}