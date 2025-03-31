package com.example.validation

/**
 * Validates UK postcodes based on general length and format rules.
 * Note: Does not check against a database of valid postcodes, only format using a regular expression.
 * Source: https://www.youtube.com/watch?v=NBL0igWs2YU
 */
class PostcodeValidator {
    
    // UK postcode regex pattern
    private val postcodeRegex = Regex("^([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([AZa-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9]?[A-Za-z]))))[0-9][A-Za-z]{2})\$")
    
    /**
     * Result class for validation outcomes
     * Use of sealed class ensure that when clause in Routing.kt check exhaustively for each case
     */
    sealed class ValidationResult {
        data object Valid : ValidationResult()
        data class Invalid(val message: String) : ValidationResult()
    }
    
    /**
     * Clean a postcode by removing whitespace
     */
    fun cleanPostcode(postcode: String): String {
        return postcode.replace("\\s".toRegex(), "")
    }
    
    /**
     * Validate the length of a postcode
     */
    private fun validateLength(postcode: String): ValidationResult {
        return when {
            postcode.length < 5 -> ValidationResult.Invalid("Postcode should be at least 5 characters")
            postcode.length > 7 -> ValidationResult.Invalid("Postcode should be at most 7 characters")
            else -> ValidationResult.Valid
        }
    }
    
    /**
     * Validate the format of a postcode
     */
    private fun validateFormat(postcode: String): ValidationResult {
        return if (postcodeRegex.matches(postcode)) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid("Enter a valid postcode")
        }
    }
    
    /**
     * Full validation of a postcode
     * Cleans the postcode first, then validates length and format
     */
    fun validate(postcode: String): ValidationResult {
        if (postcode.isBlank()) {
            return ValidationResult.Invalid("Postcode is required")
        }
        
        val cleanedPostcode = cleanPostcode(postcode)
        
        // Check length first (faster check)
        val lengthResult = validateLength(cleanedPostcode)
        if (lengthResult is ValidationResult.Invalid) {
            return lengthResult
        }
        
        // Then check format (more expensive regex check)
        return validateFormat(cleanedPostcode)
    }
} 