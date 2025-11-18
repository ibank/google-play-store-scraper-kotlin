package com.modern.playscraper.domain.usecase

import com.modern.playscraper.domain.model.AppReview
import com.modern.playscraper.domain.model.ReviewSortOrder
import com.modern.playscraper.domain.repository.PlayStoreRepository
import kotlinx.coroutines.flow.Flow

/**
 * Get app reviews UseCase
 */
class GetAppReviewsUseCase(
    private val repository: PlayStoreRepository
) {
    /**
     * Get app review list
     *
     * @param appId App ID (required)
     * @param sortOrder Sort order
     * @param language Language code
     * @param country Country code
     * @param limit Maximum number of results
     * @return Result<List<AppReview>>
     */
    suspend operator fun invoke(
        appId: String,
        sortOrder: ReviewSortOrder = ReviewSortOrder.NEWEST,
        language: String = "en",
        country: String = "us",
        limit: Int = 100
    ): Result<List<AppReview>> {
        // Input validation
        require(appId.isNotBlank()) { "App ID cannot be blank" }
        require(limit > 0) { "Limit must be greater than 0" }
        require(limit <= 1000) { "Limit cannot exceed 1000" }

        return repository.getAppReviews(appId, sortOrder, language, country, limit)
    }

    /**
     * Get app reviews as streaming (Flow)
     *
     * @param appId App ID (required)
     * @param sortOrder Sort order
     * @param language Language code
     * @param country Country code
     * @param limit Maximum number of results
     * @return Flow<AppReview>
     */
    fun asFlow(
        appId: String,
        sortOrder: ReviewSortOrder = ReviewSortOrder.NEWEST,
        language: String = "en",
        country: String = "us",
        limit: Int = 100
    ): Flow<AppReview> {
        // Input validation
        require(appId.isNotBlank()) { "App ID cannot be blank" }
        require(limit > 0) { "Limit must be greater than 0" }

        return repository.getAppReviewsFlow(appId, sortOrder, language, country, limit)
    }
}
