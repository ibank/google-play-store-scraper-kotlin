package com.modern.playscraper.di

import com.modern.playscraper.infrastructure.http.*
import com.modern.playscraper.infrastructure.parser.AppDetailsParser
import com.modern.playscraper.infrastructure.parser.AppsListParser
import com.modern.playscraper.infrastructure.parser.AppReviewsParser
import com.modern.playscraper.infrastructure.parser.JsonPathProcessor
import com.modern.playscraper.infrastructure.parser.PermissionsParser
import com.modern.playscraper.infrastructure.parser.ScriptDataParser
import com.modern.playscraper.infrastructure.spec.SpecsRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Infrastructure layer DI module
 */
val infrastructureModule = module {

    // HTTP client configuration
    single(named("throttler")) {
        HumanBehaviorRequestThrottler() as RequestThrottler
    }

    single(named("userAgentProvider")) {
        ResourceFileUserAgentProvider(
            resourcePath = "legacy_user_agents.txt",
            random = true
        ) as UserAgentProvider
    }

    single(named("retryPolicy")) {
        RetryPolicy.Default
    }

    single {
        val minimalHeaders = runCatching { get<Boolean>(named("minimalHeaders")) }.getOrDefault(true)
        val enableLogging = runCatching { get<Boolean>(named("enableLogging")) }.getOrDefault(false)

        KtorClientFactory.create(
            throttler = get(named("throttler")),
            userAgentProvider = get(named("userAgentProvider")),
            enableLogging = enableLogging,
            minimalHeaders = minimalHeaders
        )
    }

    // Parsers
    single { ScriptDataParser() }
    single { JsonPathProcessor() }
    single { SpecsRepository() }

    single {
        AppDetailsParser(
            specsRepository = get(),
            scriptDataParser = get(),
            jsonPathProcessor = get()
        )
    }

    single {
        AppsListParser(
            scriptDataParser = get(),
            jsonPathProcessor = get()
        )
    }

    single {
        AppReviewsParser(
            scriptDataParser = get(),
            jsonPathProcessor = get()
        )
    }

    single {
        PermissionsParser(
            scriptDataParser = get(),
            jsonPathProcessor = get()
        )
    }
}
