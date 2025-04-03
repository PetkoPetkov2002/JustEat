package com.example.app

import android.net.http.HttpResponseCache.install
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
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

// Data class to hold restaurant information
data class RestaurantInfo(
    val name: String,
    val cuisines: List<String>,
    val rating: Double,
    val address: String
)

interface UserService {

    @GET("/restaurants")
    suspend fun getUsers(
        @Query("per_page") per_page: Int,
        @Query("page") page: Int
    ): List<RestaurantInfo>

    @GET("/restaurants/{postcode}")
    suspend fun getUser(@Path("postcode") postcode: String)
}
interface RestaurantApiService {
    @GET("restaurants/{postcode}")
    suspend fun getRestaurants(@Path("postcode") postcode: String): List<RestaurantInfo>
}
val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("http://10.0.2.2:8080/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
val okHttpClient = OkHttpClient.Builder()
    .build()



val restaurantApiService = retrofit.create(RestaurantApiService::class.java)
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
fun RestaurantItem(restaurant: RestaurantInfo) {
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
            Text(text = "Rating: ${restaurant.cuisines}/5")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Rating: ${restaurant.rating}/5")
        }
    }
}

// Function to fetch restaurants from the API

suspend fun fetchRestaurants(query: String): List<RestaurantInfo> {
    return try {
        Log.d("RestaurantSearch", "Fetching restaurants for: $query")
        restaurantApiService.getRestaurants(query)
    } catch (e: Exception) {
        Log.e("RestaurantSearch", "Network error: ${e.message}", e)
        throw e
    }
}

@Preview(showBackground = true)
@Composable
fun RestaurantSearchPreview() {
    AppTheme {
        RestaurantSearchScreen()
    }
}