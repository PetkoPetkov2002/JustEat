package com.example.app.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.model.RestaurantInfo
import com.example.app.data.repository.RestaurantRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for the restaurant search screen.
 * Manages UI state and interacts with the data layer (RestaurantRepository).
 */
class RestaurantViewModel(application: Application) : AndroidViewModel(application) {
    // Holds the current search query entered by the user.
    val searchQuery = mutableStateOf("")
    // Holds the list of restaurants fetched from the repository.
    val restaurants = mutableStateListOf<RestaurantInfo>()
    // Indicates whether a network request is in progress.
    val isLoading = mutableStateOf(false)
    // Holds any error message to be displayed to the user.
    val errorMessage = mutableStateOf<String?>(null)

    // Instance of the repository to fetch restaurant data.
    private val repository = RestaurantRepository(application)

    /**
     * Initiates a search for restaurants based on the provided query.
     * Updates the UI state (loading, results, error) accordingly.
     *
     * @param query The postcode or search term entered by the user.
     */
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