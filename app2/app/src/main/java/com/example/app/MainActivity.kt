package com.example.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.AppTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

// Data class to hold restaurant information
data class Restaurant(
    val id: String,
    val name: String,
    val address: String,
    val rating: Float
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RestaurantSearchScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun RestaurantSearchScreen(modifier: Modifier = Modifier) {
    val searchQuery = remember { mutableStateOf("") }
    val restaurants = remember { mutableStateListOf<Restaurant>() }
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App title
        Text(
            text = "Restaurant Finder",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Search box and button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery.value,
                onValueChange = { searchQuery.value = it },
                label = { Text("Search restaurants") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            
            Button(
                onClick = {
                    // Clear previous results and errors
                    errorMessage.value = null
                    isLoading.value = true
                    restaurants.clear()
                    
                    coroutineScope.launch {
                        try {
                            val fetchedRestaurants = fetchRestaurants(searchQuery.value)
                            restaurants.addAll(fetchedRestaurants)
                        } catch (e: Exception) {
                            Log.e("RestaurantSearch", "Error fetching restaurants", e)
                            errorMessage.value = "Failed to fetch restaurants: ${e.message}"
                        } finally {
                            isLoading.value = false
                        }
                    }
                }
            ) {
                Text("Search")
            }
        }

        // Loading indicator
        if (isLoading.value) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp)
            )
        }

        // Error message
        errorMessage.value?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Restaurant list
        if (restaurants.isNotEmpty()) {
            Text(
                text = "Found ${restaurants.size} restaurants",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            LazyColumn {
                items(restaurants) { restaurant ->
                    RestaurantItem(restaurant = restaurant)
                }
            }
        }
    }
}

@Composable
fun RestaurantItem(restaurant: Restaurant) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = restaurant.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = restaurant.address)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Rating: ${restaurant.rating}/5")
        }
    }
}

// Function to fetch restaurants from the API
suspend fun fetchRestaurants(query: String): List<Restaurant> {
    // Simulate network call on a background thread
    return kotlin.runCatching {
        val url = URL("http://10.0.2.2:8000/restaurants?query=$query")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        
        try {
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                
                // Parse JSON response
                val gson = Gson()
                val restaurantType = object : TypeToken<List<Restaurant>>() {}.type
                
                try {
                    // Attempt to parse the JSON response
                    gson.fromJson<List<Restaurant>>(response.toString(), restaurantType)
                } catch (e: Exception) {
                    Log.e("RestaurantSearch", "Error parsing JSON: ${e.message}")
                    // Return mock data if parsing fails
                    listOf(
                        Restaurant("1", "Italian Delight", "123 Main St", 4.5f),
                        Restaurant("2", "Sushi Paradise", "456 Oak Ave", 4.7f),
                        Restaurant("3", "Burger Joint", "789 Pine Rd", 4.2f)
                    )
                }
            } else {
                throw Exception("HTTP error code: $responseCode")
            }
        } finally {
            connection.disconnect()
        }
    }.getOrElse { 
        Log.e("RestaurantSearch", "Network error: ${it.message}")
        throw it
    }
}

@Preview(showBackground = true)
@Composable
fun RestaurantSearchPreview() {
    AppTheme {
        RestaurantSearchScreen()
    }
}