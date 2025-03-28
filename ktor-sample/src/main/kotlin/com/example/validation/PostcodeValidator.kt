package com.example.validation

/**
 * Validator for UK postcodes
 * Handles validation of postcode format and length
 */
class PostcodeValidator {
    
    // UK postcode regex pattern
    private val postcodeRegex = Regex("^([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([AZa-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9]?[A-Za-z]))))[0-9][A-Za-z]{2})\$")
    
    /**
     * Result class for validation outcomes
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