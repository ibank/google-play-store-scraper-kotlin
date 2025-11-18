package com.modern.playscraper.domain.repository

import com.modern.playscraper.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Play Store repository interface
 *
 * Data access abstraction for the domain layer
 */
interface PlayStoreRepository {

    /**
     * Get app details
     *
     * @param appId App ID
     * @param language Language code
     * @param country Country code
     * @return Result<AppDetails>
     */
    suspend fun getAppDetails(
        appId: String,
        language: String = "en",
        country: String = "us"
    ): Result<AppDetails>

    /**
     * Get developer apps
     *
     * @param developerId Developer ID
     * @param language Language code
     * @param country Country code
     * @param limit Maximum number of results
     * @return Result<List<App>>
     */
    suspend fun getDeveloperApps(
        developerId: String,
        language: String = "en",
        country: String = "us",
        limit: Int = 120
    ): Result<List<App>>

    /**
     * Get similar apps
     *
     * @param appId App ID
     * @param language Language code
     * @param country Country code
     * @param limit Maximum number of results
     * @return Result<List<App>>
     */
    suspend fun getSimilarApps(
        appId: String,
        language: String = "en",
        country: String = "us",
        limit: Int = 120
    ): Result<List<App>>

    /**
     * Get apps by category
     *
     * @param category Category
     * @param collection Collection
     * @param language Language code
     * @param country Country code
     * @param limit Maximum number of results
     * @return Result<List<App>>
     */
    suspend fun getCategoryApps(
        category: Category,
        collection: com.modern.playscraper.domain.model.Collection = com.modern.playscraper.domain.model.Collection.TOP_FREE,
        language: String = "en",
        country: String = "us",
        limit: Int = 120
    ): Result<List<App>>

    /**
     * Search apps
     *
     * @param query Search query
     * @param language Language code
     * @param country Country code
     * @param limit Maximum number of results
     * @return Result<List<App>>
     */
    suspend fun searchApps(
        query: String,
        language: String = "en",
        country: String = "us",
        limit: Int = 120
    ): Result<List<App>>

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
    ): Result<List<Permission>>

    /**
     * Get app reviews (Flow streaming)
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
    ): Flow<AppReview>

    /**
     * Get app reviews (regular)
     *
     * @param appId App ID
     * @param sortOrder Sort order
     * @param language Language code
     * @param country Country code
     * @param limit Maximum number of results
     * @return Result<List<AppReview>>
     */
    suspend fun getAppReviews(
        appId: String,
        sortOrder: ReviewSortOrder = ReviewSortOrder.NEWEST,
        language: String = "en",
        country: String = "us",
        limit: Int = 100
    ): Result<List<AppReview>>
}
