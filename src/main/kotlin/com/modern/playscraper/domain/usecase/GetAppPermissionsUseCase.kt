package com.modern.playscraper.domain.usecase

import com.modern.playscraper.domain.model.Permission
import com.modern.playscraper.domain.repository.PlayStoreRepository

/**
 * Get app permissions UseCase
 */
class GetAppPermissionsUseCase(
    private val repository: PlayStoreRepository
) {
    /**
     * Get list of permissions requested by an app
     *
     * @param appId App ID (required)
     * @param language Language code
     * @return Result<List<Permission>>
     */
    suspend operator fun invoke(
        appId: String,
        language: String = "en"
    ): Result<List<Permission>> {
        // Input validation
        require(appId.isNotBlank()) { "App ID cannot be blank" }

        return repository.getAppPermissions(appId, language)
    }
}
