package com.modern.playscraper.di

import com.modern.playscraper.data.datasource.cache.CacheStrategy
import com.modern.playscraper.data.datasource.cache.InMemoryCache
import com.modern.playscraper.data.datasource.remote.PlayStoreRemoteDataSource
import com.modern.playscraper.data.datasource.remote.RemoteDataSource
import com.modern.playscraper.data.repository.PlayStoreRepositoryImpl
import com.modern.playscraper.domain.repository.PlayStoreRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Data layer DI module
 */
val dataModule = module {

    // Cache
    single<CacheStrategy> { InMemoryCache() }

    // Remote Data Source
    single<RemoteDataSource> {
        PlayStoreRemoteDataSource(
            httpClient = get(),
            appDetailsParser = get(),
            appsListParser = get(),
            appReviewsParser = get(),
            permissionsParser = get(),
            retryPolicy = get(named("retryPolicy"))
        )
    }

    // Repository
    single<PlayStoreRepository> {
        PlayStoreRepositoryImpl(
            remoteDataSource = get(),
            cacheStrategy = get()
        )
    }
}
