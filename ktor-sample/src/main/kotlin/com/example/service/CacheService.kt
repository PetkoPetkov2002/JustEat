import com.example.model.RestaurantInfo
import com.github.benmanes.caffeine.cache.Caffeine
import java.time.Duration // Or use kotlin.time.Duration in newer Kotlin versions

class CacheService {
    /**
     * Caffeine cache instance configured with:
     * - Time-based expiration (e.g., 30 minutes after write).
     * - Maximum size limit (e.g., 1000 entries).
     */
    private val cache = Caffeine.newBuilder()
        // Set cache entries to expire 30 minutes after they are last written
        .expireAfterWrite(Duration.ofMinutes(30))
        // Set a maximum size to prevent memory issues
        .maximumSize(1000) // Store up to 1000 postcode entries
        // Build the cache specifying key (String for postcode) and value (List<RestaurantInfo>) types
        .build<String, List<RestaurantInfo>>()
    /**
     * Retrieves cached restaurant data for the given postcode, if present and not expired.
     * @param postcode The key (postcode) to look up.
     * @return The cached [List<RestaurantInfo>] or null if not found or expired.
     */
    fun get(postcode: String): List<RestaurantInfo>? {
        // .getIfPresent returns null if the key is not found or expired
        return cache.getIfPresent(postcode)
    }

     /**
     * Adds or updates an entry in the cache.
     * @param postcode The key (postcode) to store the data under.
     * @param restaurants The [List<RestaurantInfo>] data to cache.
     */
    fun set(postcode: String, restaurants: List<RestaurantInfo>) {
        // .put adds or updates the entry in the cache
        cache.put(postcode, restaurants)
    }

    /**
     * Manually removes a specific postcode entry from the cache.
     * @param postcode The key (postcode) of the entry to remove.
     */
    fun invalidate(postcode: String) {
        cache.invalidate(postcode)
    }
}