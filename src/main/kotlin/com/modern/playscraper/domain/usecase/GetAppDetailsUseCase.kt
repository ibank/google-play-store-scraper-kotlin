package com.modern.playscraper.domain.usecase

import com.modern.playscraper.domain.model.AppDetails
import com.modern.playscraper.domain.repository.PlayStoreRepository

/**
 * Get app details UseCase
 */
class GetAppDetailsUseCase(
    private val repository: PlayStoreRepository
) {
    /**
     * Get app details
     *
     * @param appId App ID (required)
     * @param language Language code
     * @param country Country code
     * @return Result<AppDetails>
     */
    suspend operator fun invoke(
        appId: String,
        language: String = "en",
        country: String = "us"
    ): Result<AppDetails> {
        // Input validation
        require(appId.isNotBlank()) { "App ID cannot be blank" }
        require(language.isNotBlank()) { "Language cannot be blank" }
        require(country.isNotBlank()) { "Country cannot be blank" }

        return repository.getAppDetails(appId, language, country)
    }
}
