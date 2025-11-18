package com.modern.playscraper.data.datasource.remote

import com.modern.playscraper.domain.model.*

/**
 * Remote data source interface
 */
interface RemoteDataSource {

    /**
     * Fetch app details
     *
     * @param appId App ID
     * @param language Language code (e.g., "en", "ko")
     * @param country Country code (e.g., "us", "kr")
     * @return Result<AppDetails>
     */
    suspend fun fetchAppDetails(
        appId: String,
        language: String = "en",
        country: String = "us"
    ): Result<AppDetails>

    /**
     * Fetch developer app list
     *
     * @param developerId Developer ID
     * @param language Language code
     * @param country Country code
     * @param limit Maximum count
     * @return Result<List<App>>
     */
    suspend fun fetchDeveloperApps(
        developerId: String,
        language: String = "en",
        country: String = "us",
        limit: Int = 120
    ): Result<List<App>>

    /**
     * Fetch similar app list
     *
     * @param appId App ID
     * @param language Language code
     * @param country Country code
     * @param limit Maximum count
     * @return Result<List<App>>
     */
    suspend fun fetchSimilarApps(
        appId: String,
        language: String = "en",
        country: String = "us",
        limit: Int = 120
    ): Result<List<App>>

    /**
     * Fetch app list by category
     *
     * @param category Category
     * @param collection Collection
     * @param language Language code
     * @param country Country code
     * @param limit Maximum count
     * @return Result<List<App>>
     */
    suspend fun fetchCategoryApps(
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
     * @param limit Maximum count
     * @return Result<List<App>>
     */
    suspend fun searchApps(
        query: String,
        language: String = "en",
        country: String = "us",
        limit: Int = 120
    ): Result<List<App>>

    /**
     * Fetch app permissions
     *
     * @param appId App ID
     * @param language Language code
     * @return Result<List<Permission>>
     */
    suspend fun fetchAppPermissions(
        appId: String,
        language: String = "en"
    ): Result<List<Permission>>

    /**
     * Fetch app reviews
     *
     * @param appId App ID
     * @param sortOrder Sort order
     * @param language Language code
     * @param country Country code
     * @param limit Maximum count
     * @return Result<List<AppReview>>
     */
    suspend fun fetchAppReviews(
        appId: String,
        sortOrder: ReviewSortOrder = ReviewSortOrder.NEWEST,
        language: String = "en",
        country: String = "us",
        limit: Int = 100
    ): Result<List<AppReview>>
}
