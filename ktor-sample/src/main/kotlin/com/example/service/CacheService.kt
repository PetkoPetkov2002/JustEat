import com.example.model.RestaurantInfo
import com.github.benmanes.caffeine.cache.Caffeine
import java.time.Duration // Or use kotlin.time.Duration in newer Kotlin versions

class CacheService {
    // Configure the cache
    private val cache = Caffeine.newBuilder()
        // Set cache entries to expire 30 minutes after they are last written
        .expireAfterWrite(Duration.ofMinutes(30))
        // Set a maximum size to prevent memory issues
        .maximumSize(1000) // Store up to 1000 postcode entries
        // Build the cache specifying key (String for postcode) and value (List<RestaurantInfo>) types
        .build<String, List<RestaurantInfo>>()

    /**
     * Retrieves restaurant data from the cache for a given postcode.
     * Returns null if the postcode is not in the cache or has expired.
     */
    fun get(postcode: String): List<RestaurantInfo>? {
        // .getIfPresent returns null if the key is not found or expired
        return cache.getIfPresent(postcode)
    }

    /**
     * Stores restaurant data in the cache for a given postcode.
     * If the postcode already exists, its value and expiry time will be updated.
     */
    fun set(postcode: String, restaurants: List<RestaurantInfo>) {
        // .put adds or updates the entry in the cache
        cache.put(postcode, restaurants)
    }

    /**
     * Manually removes an entry from the cache.
     * Useful if you need to force a refresh for a specific postcode.
     */
    fun invalidate(postcode: String) {
        cache.invalidate(postcode)
    }
}