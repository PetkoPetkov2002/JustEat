package com.example.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import com.example.app.viewmodel.RestaurantViewModel
import kotlinx.coroutines.launch

// Remove duplicate ErrorResponse class

/**
 * The main activity of the application, hosting the Jetpack Compose UI.
 */
class MainActivity : ComponentActivity() {
    // Use the viewModels() delegate to lazily initialize the RestaurantViewModel.
    // The ViewModel's lifecycle is tied to the Activity.
    private val viewModel: RestaurantViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RestaurantSearchScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel  // Pass viewModel instead of repository
                    )
                }
            }
        }
    }
}

/**
 * The main composable function for the restaurant search screen.
 * It observes state from the ViewModel and displays the UI accordingly.
 *
 * @param modifier Modifier for layout customization.
 * @param viewModel The instance of [RestaurantViewModel] providing the state.
 */
@Composable
fun RestaurantSearchScreen(
    modifier: Modifier = Modifier,
    viewModel: RestaurantViewModel  // Accept viewModel instead of repository
) {
    // Access ViewModel state
    val searchQuery = viewModel.searchQuery
    val restaurants = viewModel.restaurants
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

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
                    // Use viewModel function to search
                    viewModel.searchRestaurants(searchQuery.value)
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

/**
 * Composable function to display a single restaurant's information in a Card.
 *
 * @param restaurant The [RestaurantInfo] object containing the data to display.
 */
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

/**
 * Preview function for the [RestaurantSearchScreen] composable.
 * Provides sample data for UI development and testing in Android Studio.
 */
@Preview(showBackground = true)
@Composable
fun RestaurantSearchPreview() {
    // Preview cannot use real viewModel, so create one just for preview
    val previewContext = LocalContext.current
    val previewViewModel = remember { 
        RestaurantViewModel(previewContext.applicationContext as android.app.Application)
    }
    
    AppTheme {
        RestaurantSearchScreen(viewModel = previewViewModel)
    }
}