package com.example

import com.example.model.RestaurantInfo
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import junit.framework.TestCase.assertFalse
import kotlinx.serialization.json.Json
import kotlin.test.*

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

    @Test
    fun testRestaurantEndpointOutputStructure() = testApplication {
        application {
            configureRouting()
            configureSerialization()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        // Make the request and get the response
        val response = client.get("/restaurants/LL574BB") {
            accept(ContentType.Application.Json)
        }
        //Retrieve the body and deserialize to RestaurantInfo
        val model_response: List<RestaurantInfo> = response.body()

        // Verify it's an array of RestuarantInfo Objects
       assertIs<List<RestaurantInfo>>(model_response)

    }

    @Test
    fun testPostCodeLowerBoundary() = testApplication {
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
        val response = client.get("/restaurants/LL")
        assertEquals(HttpStatusCode.BadRequest, response.status,message = "Postcode must be between 5-7 characters")


    }
    @Test
    fun testPostCodeUpperBoundary() = testApplication {
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
        val response = client.get("/restaurants/LL574BBB")
        assertEquals(HttpStatusCode.BadRequest, response.status,message = "Postcode must be between 5-7 characters")
    }

}
