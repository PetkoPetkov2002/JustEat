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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.example.app.model.RestaurantInfo
import com.example.app.ui.theme.AppTheme
import com.example.app.data.repository.RestaurantRepository
import com.example.app.model.ErrorResponse
import kotlinx.coroutines.launch

// Keep the model classes for backward compatibility
// But remove duplicate API service since we'll use the one in Repository
data class ErrorResponse(val message: String)

class MainActivity : ComponentActivity() {
    // Create repository instance
    private lateinit var repository: RestaurantRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize repository
        repository = RestaurantRepository(applicationContext)

        enableEdgeToEdge()
        setContent {
            AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RestaurantSearchScreen(
                        modifier = Modifier.padding(innerPadding),
                        repository = repository
                    )
                }
            }
        }
    }
}

@Composable
fun RestaurantSearchScreen(
    modifier: Modifier = Modifier,
    repository: RestaurantRepository
) {
    val searchQuery = remember { mutableStateOf("") }
    val restaurants = remember { mutableStateListOf<RestaurantInfo>() }
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
                            // Use repository instead of direct API call
                            val fetchedRestaurants = repository.getRestaurants(searchQuery.value)
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
fun RestaurantItem(restaurant: RestaurantInfo) {
    // Keep your original RestaurantItem implementation unchanged
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
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Address: ")
                    }
                    append(restaurant.address)
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Cuisines: ")
                    }
                    append(restaurant.cuisines.joinToString(", "))
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Rating: ")
                    }
                    append("${restaurant.rating}/5")
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RestaurantSearchPreview() {
    AppTheme {
        // For preview purposes only
        RestaurantSearchScreen(repository = RestaurantRepository(LocalContext.current))
    }
}