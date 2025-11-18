package com.modern.playscraper.domain.usecase

import com.modern.playscraper.domain.model.App
import com.modern.playscraper.domain.model.Category
import com.modern.playscraper.domain.model.Collection
import com.modern.playscraper.domain.repository.PlayStoreRepository

/**
 * Get apps by category UseCase
 */
class GetCategoryAppsUseCase(
    private val repository: PlayStoreRepository
) {
    /**
     * Get app list for a specific category
     *
     * @param category Category (required)
     * @param collection Collection (TOP_FREE, TOP_PAID, GROSSING, etc.)
     * @param language Language code
     * @param country Country code
     * @param limit Maximum number of results
     * @return Result<List<App>>
     */
    suspend operator fun invoke(
        category: Category,
        collection: Collection = Collection.TOP_FREE,
        language: String = "en",
        country: String = "us",
        limit: Int = 120
    ): Result<List<App>> {
        // Input validation
        require(limit > 0) { "Limit must be greater than 0" }
        require(limit <= 500) { "Limit cannot exceed 500" }

        return repository.getCategoryApps(category, collection, language, country, limit)
    }
}
