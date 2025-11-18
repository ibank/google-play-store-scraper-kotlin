package com.modern.playscraper.data.repository

import com.modern.playscraper.data.datasource.cache.CacheStrategy
import com.modern.playscraper.data.datasource.remote.RemoteDataSource
import com.modern.playscraper.domain.model.*
import com.modern.playscraper.domain.repository.PlayStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

/**
 * Play Store repository implementation
 *
 * Retrieves data using cache-first strategy.
 */
class PlayStoreRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val cacheStrategy: CacheStrategy
) : PlayStoreRepository {

    override suspend fun getAppDetails(
        appId: String,
        language: String,
        country: String
    ): Result<AppDetails> {
        val cacheKey = buildCacheKey("app_details", appId, language, country)

        // Check cache
        cacheStrategy.get<AppDetails>(cacheKey)?.let {
            return Result.success(it)
        }

        // Fetch from remote
        return remoteDataSource.fetchAppDetails(appId, language, country)
            .onSuccess { details ->
                // Store in cache (1 hour TTL)
                cacheStrategy.put(cacheKey, details, ttlMs = 3600_000)
            }
    }

    override suspend fun getDeveloperApps(
        developerId: String,
        language: String,
        country: String,
        limit: Int
    ): Result<List<App>> {
        val cacheKey = buildCacheKey("developer_apps", developerId, language, country, limit.toString())

        // Check cache
        cacheStrategy.get<List<App>>(cacheKey)?.let {
            return Result.success(it)
        }

        // Fetch from remote
        return remoteDataSource.fetchDeveloperApps(developerId, language, country, limit)
            .onSuccess { apps ->
                // Store in cache (30 minutes TTL)
                cacheStrategy.put(cacheKey, apps, ttlMs = 1800_000)
            }
    }

    override suspend fun getSimilarApps(
        appId: String,
        language: String,
        country: String,
        limit: Int
    ): Result<List<App>> {
        val cacheKey = buildCacheKey("similar_apps", appId, language, country, limit.toString())

        // Check cache
        cacheStrategy.get<List<App>>(cacheKey)?.let {
            return Result.success(it)
        }

        // Fetch from remote
        return remoteDataSource.fetchSimilarApps(appId, language, country, limit)
            .onSuccess { apps ->
                // Store in cache (1 hour TTL)
                cacheStrategy.put(cacheKey, apps, ttlMs = 3600_000)
            }
    }

    override suspend fun getCategoryApps(
        category: Category,
        collection: com.modern.playscraper.domain.model.Collection,
        language: String,
        country: String,
        limit: Int
    ): Result<List<App>> {
        val cacheKey = buildCacheKey(
            "category_apps",
            category.id,
            collection.id,
            language,
            country,
            limit.toString()
        )

        // Check cache
        cacheStrategy.get<List<App>>(cacheKey)?.let {
            return Result.success(it)
        }

        // Fetch from remote
        return remoteDataSource.fetchCategoryApps(category, collection, language, country, limit)
            .onSuccess { apps ->
                // Store in cache (15 minutes TTL - frequently changed)
                cacheStrategy.put(cacheKey, apps, ttlMs = 900_000)
            }
    }

    override suspend fun searchApps(
        query: String,
        language: String,
        country: String,
        limit: Int
    ): Result<List<App>> {
        val cacheKey = buildCacheKey("search", query, language, country, limit.toString())

        // Check cache
        cacheStrategy.get<List<App>>(cacheKey)?.let {
            return Result.success(it)
        }

        // Fetch from remote
        return remoteDataSource.searchApps(query, language, country, limit)
            .onSuccess { apps ->
                // Store in cache (10 minutes TTL)
                cacheStrategy.put(cacheKey, apps, ttlMs = 600_000)
            }
    }

    override suspend fun getAppPermissions(
        appId: String,
        language: String
    ): Result<List<Permission>> {
        val cacheKey = buildCacheKey("permissions", appId, language)

        // Check cache
        cacheStrategy.get<List<Permission>>(cacheKey)?.let {
            return Result.success(it)
        }

        // Fetch from remote
        return remoteDataSource.fetchAppPermissions(appId, language)
            .onSuccess { permissions ->
                // Store in cache (1 day TTL - rarely changed)
                cacheStrategy.put(cacheKey, permissions, ttlMs = 86400_000)
            }
    }

    override fun getAppReviewsFlow(
        appId: String,
        sortOrder: ReviewSortOrder,
        language: String,
        country: String,
        limit: Int
    ): Flow<AppReview> {
        // Flow-based streaming is not cached
        return kotlinx.coroutines.flow.flow {
            val result = remoteDataSource.fetchAppReviews(appId, sortOrder, language, country, limit)
            result.getOrDefault(emptyList()).forEach { review ->
                emit(review)
            }
        }
    }

    override suspend fun getAppReviews(
        appId: String,
        sortOrder: ReviewSortOrder,
        language: String,
        country: String,
        limit: Int
    ): Result<List<AppReview>> {
        val cacheKey = buildCacheKey(
            "reviews",
            appId,
            sortOrder.name,
            language,
            country,
            limit.toString()
        )

        // Check cache
        cacheStrategy.get<List<AppReview>>(cacheKey)?.let {
            return Result.success(it)
        }

        // Fetch from remote
        return remoteDataSource.fetchAppReviews(appId, sortOrder, language, country, limit)
            .onSuccess { reviews ->
                // Store in cache (5 minutes TTL - frequently changed)
                cacheStrategy.put(cacheKey, reviews, ttlMs = 300_000)
            }
    }

    // Helper methods

    private fun buildCacheKey(vararg parts: String): String {
        return parts.joinToString(":")
    }
}
