package com.modern.playscraper.domain.usecase

import com.modern.playscraper.domain.model.App
import com.modern.playscraper.domain.repository.PlayStoreRepository

/**
 * Get developer apps UseCase
 */
class GetDeveloperAppsUseCase(
    private val repository: PlayStoreRepository
) {
    /**
     * Get app list published by developer
     *
     * @param developerId Developer ID (required)
     * @param language Language code
     * @param country Country code
     * @param limit Maximum count
     * @return Result<List<App>>
     */
    suspend operator fun invoke(
        developerId: String,
        language: String = "en",
        country: String = "us",
        limit: Int = 120
    ): Result<List<App>> {
        // Input validation
        require(developerId.isNotBlank()) { "Developer ID cannot be blank" }
        require(limit > 0) { "Limit must be greater than 0" }
        require(limit <= 500) { "Limit cannot exceed 500" }

        return repository.getDeveloperApps(developerId, language, country, limit)
    }
}
