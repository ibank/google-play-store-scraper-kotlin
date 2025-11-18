package com.modern.playscraper.domain.usecase

import com.modern.playscraper.domain.model.App
import com.modern.playscraper.domain.repository.PlayStoreRepository

/**
 * Get similar apps UseCase
 */
class GetSimilarAppsUseCase(
    private val repository: PlayStoreRepository
) {
    /**
     * Get apps similar to a specific app
     *
     * @param appId App ID (required)
     * @param language Language code
     * @param country Country code
     * @param limit Maximum number of results
     * @return Result<List<App>>
     */
    suspend operator fun invoke(
        appId: String,
        language: String = "en",
        country: String = "us",
        limit: Int = 120
    ): Result<List<App>> {
        // Input validation
        require(appId.isNotBlank()) { "App ID cannot be blank" }
        require(limit > 0) { "Limit must be greater than 0" }
        require(limit <= 500) { "Limit cannot exceed 500" }

        return repository.getSimilarApps(appId, language, country, limit)
    }
}
