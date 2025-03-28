package com.example

import com.example.service.JustEatService
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.event.*

fun Application.configureRouting() {
    install(RequestValidation) {
        validate<String> { bodyText ->
            if (!bodyText.startsWith("Hello"))
                ValidationResult.Invalid("Body text should start with 'Hello'")
            else ValidationResult.Valid
        }
    }
    
    val justEatService = JustEatService()
    
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        
        // Just Eat API endpoint
        get("/restaurants/{postcode}") {
            val postcode = call.parameters["postcode"]

            if (postcode.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Postcode is required")
                return@get
            }
            val cleanedPostcode = postcode.replace("\\s".toRegex(), "")
            val regex = Regex("^([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([AZa-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9]?[A-Za-z]))))[0-9][A-Za-z]{2})\$")
            when {
                cleanedPostcode.length < 5 -> call.respond(HttpStatusCode.BadRequest, "Postcode should be at least 5 characters")
                cleanedPostcode.length > 7 -> call.respond(HttpStatusCode.BadRequest, "Postcode should be at most 7 characters")
                !regex.matches(cleanedPostcode) -> call.respond(HttpStatusCode.BadRequest, "Enter a valid postcode")
                else-> try {
                    val restaurants = justEatService.getRestaurantsByPostcode(cleanedPostcode)
                    if (restaurants.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound, "No restaurants found for postcode $postcode")
                    } else {
                        call.respond(restaurants)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error fetching restaurant data: ${e.message}")
                }
            }


        }
        
        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")
    }
}
