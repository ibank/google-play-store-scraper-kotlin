package com.modern.playscraper.domain.error

/**
 * Scraper error type definition
 */
sealed interface ScraperError {
    val message: String
    val cause: Throwable?

    /**
     * Network-related error
     */
    data class NetworkError(
        override val message: String,
        override val cause: Throwable? = null
    ) : ScraperError

    /**
     * HTTP error (4xx, 5xx)
     */
    data class HttpError(
        val statusCode: Int,
        override val message: String,
        override val cause: Throwable? = null
    ) : ScraperError

    /**
     * Response parsing error
     */
    data class ParseError(
        override val message: String,
        override val cause: Throwable? = null
    ) : ScraperError

    /**
     * Rate limit error (429, 503)
     */
    data class RateLimitError(
        val retryAfterSeconds: Long? = null,
        override val message: String = "Rate limit exceeded",
        override val cause: Throwable? = null
    ) : ScraperError

    /**
     * Resource not found (404)
     */
    data class NotFoundError(
        val resourceId: String,
        override val message: String = "Resource not found: $resourceId",
        override val cause: Throwable? = null
    ) : ScraperError

    /**
     * Generic error
     */
    data class GenericError(
        override val message: String,
        override val cause: Throwable? = null
    ) : ScraperError
}

/**
 * Convert ScraperError to Exception
 */
fun ScraperError.toException(): Exception {
    return when (this) {
        is ScraperError.NetworkError -> Exception(message, cause)
        is ScraperError.HttpError -> Exception("HTTP $statusCode: $message", cause)
        is ScraperError.ParseError -> Exception("Parse error: $message", cause)
        is ScraperError.RateLimitError -> Exception("Rate limit: $message", cause)
        is ScraperError.NotFoundError -> Exception("Not found: $message", cause)
        is ScraperError.GenericError -> Exception(message, cause)
    }
}
