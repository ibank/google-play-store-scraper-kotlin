package com.modern.playscraper.di

import org.koin.dsl.module

/**
 * Application-wide DI module
 *
 * Integrates all modules.
 */
val appModule = module {
    includes(
        infrastructureModule,
        dataModule,
        domainModule
    )
}

/**
 * List of all modules
 */
val allModules = listOf(
    infrastructureModule,
    dataModule,
    domainModule
)
