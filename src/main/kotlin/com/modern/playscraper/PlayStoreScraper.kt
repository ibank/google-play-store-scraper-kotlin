package com.modern.playscraper

import com.modern.playscraper.data.datasource.cache.CacheStrategy
import com.modern.playscraper.data.datasource.cache.InMemoryCache
import com.modern.playscraper.data.datasource.cache.NoCaching
import com.modern.playscraper.di.allModules
import com.modern.playscraper.domain.model.*
import com.modern.playscraper.domain.usecase.*
import com.modern.playscraper.infrastructure.http.HumanBehaviorRequestThrottler
import com.modern.playscraper.infrastructure.http.NoRequestThrottling
import com.modern.playscraper.infrastructure.http.RequestThrottler
import com.modern.playscraper.infrastructure.http.RetryPolicy
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

/**
 * Play Store Scraper - Public API
 *
 * Main entry point for the library that scrapes app information from Google Play Store.
 *
 * ## Usage Example
 *
 * ```kotlin
 * // Create scraper with default settings
 * val scraper = PlayStoreScraper()
 *
 * // Get app details
 * val result = scraper.getAppDetails("com.example.app")
 * result.onSuccess { details ->
 *     println("App: ${details.title}")
 *     println("Rating: ${details.score}")
 * }
 *
 * // Custom configuration
 * val scraper = PlayStoreScraper(
 *     config = PlayStoreScraper.Config(
 *         enableCache = true,
 *         throttler = HumanBehaviorRequestThrottler(),
 *         retryPolicy = RetryPolicy.Aggressive
 *     )
 * )
 * ```
 */
class PlayStoreScraper(
    config: Config = Config()
) : KoinComponent {

    /**
     * Scraper configuration
     *
     * @property enableCache Enable caching
     * @property cacheStrategy Cache strategy (uses default InMemoryCache if null)
     * @property throttler Request throttler
     * @property retryPolicy Retry policy
     * @property enableLogging Enable HTTP logging
     * @property minimalHeaders Use standard HTTP headers (default: true)
     */
    data class Config(
        val enableCache: Boolean = true,
        val cacheStrategy: CacheStrategy? = null,
        val throttler: RequestThrottler = HumanBehaviorRequestThrottler(),
        val retryPolicy: RetryPolicy = RetryPolicy.Default,
        val enableLogging: Boolean = false,
        val minimalHeaders: Boolean = true
    )

    // Use Cases (injected by Koin)
    private val getAppDetailsUseCase: GetAppDetailsUseCase by inject()
    private val getDeveloperAppsUseCase: GetDeveloperAppsUseCase by inject()
    private val getSimilarAppsUseCase: GetSimilarAppsUseCase by inject()
    private val getCategoryAppsUseCase: GetCategoryAppsUseCase by inject()
    private val searchAppsUseCase: SearchAppsUseCase by inject()
    private val getAppPermissionsUseCase: GetAppPermissionsUseCase by inject()
    private val getAppReviewsUseCase: GetAppReviewsUseCase by inject()

    init {
        // Initialize Koin
        startKoin {
            modules(allModules)
            modules(configModule(config))
        }
    }

    /**
     * Get app details
     *
     * @param appId App ID (package name, e.g., "com.example.app")
     * @param language Language code (default: "en")
     * @param country Country code (default: "us")
     * @return Result<AppDetails>
     */
    suspend fun getAppDetails(
        appId: String,
        language: String = "en",
        country: String = "us"
    ): Result<AppDetails> {
        return getAppDetailsUseCase(appId, language, country)
    }

    /**
     * Get developer apps
     *
     * @param developerId Developer ID
     * @param language Language code
     * @param country Country code
     * @param limit Maximum number of results (default: 120)
     * @return Result<List<App>>
     */
    suspend fun getDeveloperApps(
        developerId: String,
        language: String = "en",
        country: String = "us",
        limit: Int = 120
    ): Result<List<App>> {
        return getDeveloperAppsUseCase(developerId, language, country, limit)
    }

    /**
     * Get similar apps
     *
     * @param appId App ID
     * @param language Language code
     * @param country Country code
     * @param limit Maximum number of results (default: 120)
     * @return Result<List<App>>
     */
    suspend fun getSimilarApps(
        appId: String,
        language: String = "en",
        country: String = "us",
        limit: Int = 120
    ): Result<List<App>> {
        return getSimilarAppsUseCase(appId, language, country, limit)
    }

    /**
     * Get apps by category
     *
     * @param category Category
     * @param collection Collection (TOP_FREE, TOP_PAID, GROSSING, etc.)
     * @param language Language code
     * @param country Country code
     * @param limit Maximum number of results (default: 120)
     * @return Result<List<App>>
     */
    suspend fun getCategoryApps(
        category: Category,
        collection: com.modern.playscraper.domain.model.Collection = com.modern.playscraper.domain.model.Collection.TOP_FREE,
        language: String = "en",
        country: String = "us",
        limit: Int = 120
    ): Result<List<App>> {
        return getCategoryAppsUseCase(category, collection, language, country, limit)
    }

    /**
     * Search apps
     *
     * @param query Search query
     * @param language Language code
     * @param country Country code
     * @param limit Maximum number of results (default: 120)
     * @return Result<List<App>>
     */
    suspend fun searchApps(
        query: String,
        language: String = "en",
        country: String = "us",
        limit: Int = 120
    ): Result<List<App>> {
        return searchAppsUseCase(query, language, country, limit)
    }

    /**
     * Get app permissions
     *
     * @param appId App ID
     * @param language Language code
     * @return Result<List<Permission>>
     */
    suspend fun getAppPermissions(
        appId: String,
        language: String = "en"
    ): Result<List<Permission>> {
        return getAppPermissionsUseCase(appId, language)
    }

    /**
     * Get app reviews
     *
     * @param appId App ID
     * @param sortOrder Sort order (NEWEST, MOST_HELPFUL, RATING)
     * @param language Language code
     * @param country Country code
     * @param limit Maximum number of results (default: 100)
     * @return Result<List<AppReview>>
     */
    suspend fun getAppReviews(
        appId: String,
        sortOrder: ReviewSortOrder = ReviewSortOrder.NEWEST,
        language: String = "en",
        country: String = "us",
        limit: Int = 100
    ): Result<List<AppReview>> {
        return getAppReviewsUseCase(appId, sortOrder, language, country, limit)
    }

    /**
     * Get app reviews as streaming (Flow)
     *
     * @param appId App ID
     * @param sortOrder Sort order
     * @param language Language code
     * @param country Country code
     * @param limit Maximum number of results
     * @return Flow<AppReview>
     */
    fun getAppReviewsFlow(
        appId: String,
        sortOrder: ReviewSortOrder = ReviewSortOrder.NEWEST,
        language: String = "en",
        country: String = "us",
        limit: Int = 100
    ): Flow<AppReview> {
        return getAppReviewsUseCase.asFlow(appId, sortOrder, language, country, limit)
    }

    /**
     * Clean up resources
     */
    fun close() {
        stopKoin()
    }

    companion object {
        /**
         * Create Koin module based on configuration
         */
        private fun configModule(config: Config) = module {
            // Cache Strategy Override
            single<CacheStrategy> {
                when {
                    !config.enableCache -> NoCaching
                    config.cacheStrategy != null -> config.cacheStrategy
                    else -> InMemoryCache()
                }
            }

            // Throttler Override
            single<RequestThrottler> {
                config.throttler
            }

            // Retry Policy Override
            single<RetryPolicy> {
                config.retryPolicy
            }

            // Minimal Headers Configuration
            single(org.koin.core.qualifier.named("minimalHeaders")) {
                config.minimalHeaders
            }

            // Logging Configuration
            single(org.koin.core.qualifier.named("enableLogging")) {
                config.enableLogging
            }
        }
    }
}
