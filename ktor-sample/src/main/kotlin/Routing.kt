package com.example

import com.example.service.JustEatService
import com.example.validation.PostcodeValidator
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureRouting() {

    
    val justEatService = JustEatService()
    val postcodeValidator = PostcodeValidator()
    
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
            
            // Validate the postcode
            when (val result = postcodeValidator.validate(postcode)) {
                is PostcodeValidator.ValidationResult.Valid -> {
                    try {
                        val cleanedPostcode = postcodeValidator.cleanPostcode(postcode)
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
                is PostcodeValidator.ValidationResult.Invalid -> {
                    call.respond(HttpStatusCode.BadRequest, result.message)
                }
            }
        }
        
        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")
    }
}
