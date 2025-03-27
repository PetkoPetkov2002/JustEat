package com.example

import com.example.model.RestaurantInfo
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testRestaurantEndpoint() = testApplication {
        application {
            configureRouting()
            configureSerialization()
            configureMonitoring()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        // Test with valid postcode
        val response = client.get("/restaurants/LL574BB")
        assertEquals(HttpStatusCode.OK, response.status)
        
        // Parse the response body
        val restaurants = Json.decodeFromString<List<RestaurantInfo>>(response.bodyAsText())
        
        // Verify response content
        assertNotEquals(0, restaurants.size)
        assertTrue(restaurants.size <= 10)
    }

}
