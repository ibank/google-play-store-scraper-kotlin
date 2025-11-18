package com.modern.playscraper.di

import com.modern.playscraper.domain.usecase.*
import org.koin.dsl.module

/**
 * Domain layer DI module
 */
val domainModule = module {

    // Use Cases
    factory { GetAppDetailsUseCase(repository = get()) }
    factory { GetDeveloperAppsUseCase(repository = get()) }
    factory { GetSimilarAppsUseCase(repository = get()) }
    factory { GetCategoryAppsUseCase(repository = get()) }
    factory { SearchAppsUseCase(repository = get()) }
    factory { GetAppPermissionsUseCase(repository = get()) }
    factory { GetAppReviewsUseCase(repository = get()) }
}
