package com.modern.playscraper.domain.usecase

import com.modern.playscraper.domain.model.App
import com.modern.playscraper.domain.repository.PlayStoreRepository

/**
 * Search apps UseCase
 */
class SearchAppsUseCase(
    private val repository: PlayStoreRepository
) {
    /**
     * Search apps by query
     *
     * @param query Search query (required, minimum 2 characters)
     * @param language Language code
     * @param country Country code
     * @param limit Maximum count
     * @return Result<List<App>>
     */
    suspend operator fun invoke(
        query: String,
        language: String = "en",
        country: String = "us",
        limit: Int = 120
    ): Result<List<App>> {
        // Input validation
        require(query.isNotBlank()) { "Search query cannot be blank" }
        require(query.length >= 2) { "Search query must be at least 2 characters" }
        require(limit > 0) { "Limit must be greater than 0" }
        require(limit <= 500) { "Limit cannot exceed 500" }

        return repository.searchApps(query, language, country, limit)
    }
}
