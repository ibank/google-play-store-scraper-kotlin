package com.modern.playscraper.infrastructure.http

import com.modern.playscraper.domain.error.ScraperError
import io.ktor.client.plugins.*
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.math.pow

/**
 * Retry policy configuration
 *
 * @property maxAttempts Maximum number of attempts
 * @property initialDelayMs Initial delay time (milliseconds)
 * @property maxDelayMs Maximum delay time (milliseconds)
 * @property multiplier Delay time multiplier
 * @property retryableErrors Retryable error types
 */
data class RetryPolicy(
    val maxAttempts: Int = 3,
    val initialDelayMs: Long = 1000,
    val maxDelayMs: Long = 10000,
    val multiplier: Double = 2.0,
    val retryableErrors: Set<Int> = setOf(408, 429, 500, 502, 503, 504)
) {
    init {
        require(maxAttempts > 0) { "maxAttempts must be greater than 0" }
        require(initialDelayMs >= 0) { "initialDelayMs must be greater than or equal to 0" }
        require(maxDelayMs >= initialDelayMs) { "maxDelayMs must be greater than or equal to initialDelayMs" }
        require(multiplier >= 1.0) { "multiplier must be greater than or equal to 1.0" }
    }

    /**
     * Calculate delay time for specific attempt number
     */
    fun calculateDelay(attempt: Int): Long {
        val exponentialDelay = initialDelayMs * multiplier.pow(attempt.toDouble()).toLong()
        return min(exponentialDelay, maxDelayMs)
    }

    /**
     * Check if error is retryable
     */
    fun isRetryable(error: ScraperError): Boolean {
        return when (error) {
            is ScraperError.RateLimitError -> true
            is ScraperError.HttpError -> error.statusCode in retryableErrors
            is ScraperError.NetworkError -> true
            else -> false
        }
    }

    companion object {
        /**
         * Default retry policy
         */
        val Default = RetryPolicy()

        /**
         * Aggressive retry policy (fast retry, many attempts)
         */
        val Aggressive = RetryPolicy(
            maxAttempts = 5,
            initialDelayMs = 500,
            maxDelayMs = 5000,
            multiplier = 1.5
        )

        /**
         * Conservative retry policy (slow retry, few attempts)
         */
        val Conservative = RetryPolicy(
            maxAttempts = 2,
            initialDelayMs = 2000,
            maxDelayMs = 20000,
            multiplier = 3.0
        )

        /**
         * No retry
         */
        val NoRetry = RetryPolicy(
            maxAttempts = 1,
            initialDelayMs = 0,
            maxDelayMs = 0,
            multiplier = 1.0
        )
    }
}

/**
 * Retry function using Exponential Backoff
 *
 * @param policy Retry policy
 * @param block Block to execute
 * @return Execution result
 */
suspend fun <T> retryWithExponentialBackoff(
    policy: RetryPolicy = RetryPolicy.Default,
    block: suspend (attempt: Int) -> T
): T {
    var lastException: Exception? = null

    repeat(policy.maxAttempts) { attempt ->
        try {
            return block(attempt)
        } catch (e: Exception) {
            lastException = e

            // Throw exception if this is the last attempt
            if (attempt == policy.maxAttempts - 1) {
                throw e
            }

            // Convert to ScraperError and check if retryable
            val scraperError = e.toScraperError()
            if (!policy.isRetryable(scraperError)) {
                throw e
            }

            // If rate limit error, wait for server-specified time
            if (scraperError is ScraperError.RateLimitError && scraperError.retryAfterSeconds != null) {
                delay(scraperError.retryAfterSeconds * 1000)
            } else {
                // Exponential backoff delay
                delay(policy.calculateDelay(attempt))
            }
        }
    }

    // All retry attempts failed
    throw lastException ?: Exception("All retry attempts failed")
}

/**
 * Convert Exception to ScraperError
 */
private fun Exception.toScraperError(): ScraperError {
    return when (this) {
        is ResponseException -> {
            val statusCode = this.response.status.value
            when (statusCode) {
                429, 503 -> ScraperError.RateLimitError(
                    retryAfterSeconds = this.response.headers["Retry-After"]?.toLongOrNull(),
                    message = "Rate limit exceeded: $statusCode",
                    cause = this
                )
                404 -> ScraperError.NotFoundError(
                    resourceId = "unknown",
                    message = "Resource not found",
                    cause = this
                )
                else -> ScraperError.HttpError(
                    statusCode = statusCode,
                    message = "HTTP error: $statusCode",
                    cause = this
                )
            }
        }
        is HttpRequestTimeoutException -> ScraperError.NetworkError(
            message = "Request timeout",
            cause = this
        )
        else -> ScraperError.NetworkError(
            message = this.message ?: "Unknown network error",
            cause = this
        )
    }
}
