package com.modern.playscraper.data.datasource.remote

import com.modern.playscraper.domain.error.ScraperError
import com.modern.playscraper.domain.model.*
import com.modern.playscraper.infrastructure.http.RetryPolicy
import com.modern.playscraper.infrastructure.http.retryWithExponentialBackoff
import com.modern.playscraper.infrastructure.parser.AppDetailsParser
import com.modern.playscraper.infrastructure.parser.AppsListParser
import com.modern.playscraper.infrastructure.parser.AppReviewsParser
import com.modern.playscraper.infrastructure.parser.PermissionsParser
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

/**
 * Google Play Store remote data source implementation
 */
class PlayStoreRemoteDataSource(
    private val httpClient: HttpClient,
    private val appDetailsParser: AppDetailsParser,
    private val appsListParser: AppsListParser,
    private val appReviewsParser: AppReviewsParser,
    private val permissionsParser: PermissionsParser,
    private val retryPolicy: RetryPolicy = RetryPolicy.Default
) : RemoteDataSource {

    override suspend fun fetchAppDetails(
        appId: String,
        language: String,
        country: String
    ): Result<AppDetails> = runCatching {
        retryWithExponentialBackoff(retryPolicy) {
            val url = buildUrl(
                path = "/store/apps/details",
                parameters = mapOf(
                    "id" to appId,
                    "hl" to language,
                    "gl" to country
                )
            )

            val html = httpClient.get(url).bodyAsText()
            appDetailsParser.parse(appId, html)
        }
    }.recoverCatching { exception ->
        throw exception.toScraperError(appId)
    }

    override suspend fun fetchDeveloperApps(
        developerId: String,
        language: String,
        country: String,
        limit: Int
    ): Result<List<App>> = runCatching {
        retryWithExponentialBackoff(retryPolicy) {
            val url = buildUrl(
                path = "/store/apps/developer",
                parameters = mapOf(
                    "id" to developerId,
                    "hl" to language,
                    "gl" to country
                )
            )

            val html = httpClient.get(url).bodyAsText()
            appsListParser.parse(html, dataSourceKey = "ds:3", appsArrayPath = listOf(1, 2, 0))
                .take(limit)
        }
    }.recoverCatching { exception ->
        throw exception.toScraperError(developerId)
    }

    override suspend fun fetchSimilarApps(
        appId: String,
        language: String,
        country: String,
        limit: Int
    ): Result<List<App>> = runCatching {
        retryWithExponentialBackoff(retryPolicy) {
            val url = buildUrl(
                path = "/store/apps/details",
                parameters = mapOf(
                    "id" to appId,
                    "hl" to language,
                    "gl" to country
                )
            )

            val html = httpClient.get(url).bodyAsText()
            // Similar apps are extracted from the app details page, typically in ds:7
            appsListParser.parse(html, dataSourceKey = "ds:7", appsArrayPath = listOf(1, 2, 0))
                .take(limit)
        }
    }.recoverCatching { exception ->
        throw exception.toScraperError(appId)
    }

    override suspend fun fetchCategoryApps(
        category: Category,
        collection: com.modern.playscraper.domain.model.Collection,
        language: String,
        country: String,
        limit: Int
    ): Result<List<App>> = runCatching {
        retryWithExponentialBackoff(retryPolicy) {
            val url = buildUrl(
                path = "/store/apps/category/${category.id}/collection/${collection.id}",
                parameters = mapOf(
                    "hl" to language,
                    "gl" to country
                )
            )

            val html = httpClient.get(url).bodyAsText()
            appsListParser.parse(html, dataSourceKey = "ds:3", appsArrayPath = listOf(1, 2, 0))
                .take(limit)
        }
    }.recoverCatching { exception ->
        throw exception.toScraperError(category.id)
    }

    override suspend fun searchApps(
        query: String,
        language: String,
        country: String,
        limit: Int
    ): Result<List<App>> = runCatching {
        retryWithExponentialBackoff(retryPolicy) {
            val url = buildUrl(
                path = "/store/search",
                parameters = mapOf(
                    "q" to query,
                    "c" to "apps",
                    "hl" to language,
                    "gl" to country
                )
            )

            val html = httpClient.get(url).bodyAsText()
            appsListParser.parseSearchResults(html)
                .take(limit)
        }
    }.recoverCatching { exception ->
        throw exception.toScraperError(query)
    }

    override suspend fun fetchAppPermissions(
        appId: String,
        language: String
    ): Result<List<Permission>> = runCatching {
        retryWithExponentialBackoff(retryPolicy) {
            val url = buildUrl(
                path = "/store/apps/datasafety",
                parameters = mapOf(
                    "id" to appId,
                    "hl" to language
                )
            )

            val html = httpClient.get(url).bodyAsText()
            permissionsParser.parse(html)
        }
    }.recoverCatching { exception ->
        throw exception.toScraperError(appId)
    }

    override suspend fun fetchAppReviews(
        appId: String,
        sortOrder: ReviewSortOrder,
        language: String,
        country: String,
        limit: Int
    ): Result<List<AppReview>> = runCatching {
        retryWithExponentialBackoff(retryPolicy) {
            val url = buildUrl(
                path = "/store/apps/details",
                parameters = mapOf(
                    "id" to appId,
                    "hl" to language,
                    "gl" to country,
                    "showAllReviews" to "true",
                    "sort" to sortOrder.value.toString()
                )
            )

            val html = httpClient.get(url).bodyAsText()
            appReviewsParser.parse(html)
                .take(limit)
        }
    }.recoverCatching { exception ->
        throw exception.toScraperError(appId)
    }

    // Helper methods

    private fun buildUrl(path: String, parameters: Map<String, String>): String {
        val baseUrl = "https://play.google.com"
        val queryString = parameters.entries.joinToString("&") { (key, value) ->
            "$key=${value.encodeURLParameter()}"
        }
        return "$baseUrl$path?$queryString"
    }

    private fun Throwable.toScraperError(resourceId: String): Throwable {
        return when (this) {
            is ScraperError -> this
            else -> ScraperError.GenericError(
                message = this.message ?: "Unknown error",
                cause = this
            )
        }.toException()
    }

    private fun ScraperError.toException(): Exception {
        return when (this) {
            is ScraperError.NetworkError -> Exception(message, cause)
            is ScraperError.HttpError -> Exception("HTTP $statusCode: $message", cause)
            is ScraperError.ParseError -> Exception("Parse error: $message", cause)
            is ScraperError.RateLimitError -> Exception("Rate limit: $message", cause)
            is ScraperError.NotFoundError -> Exception("Not found: $message", cause)
            is ScraperError.GenericError -> Exception(message, cause)
        }
    }

    companion object {
        private const val BASE_URL = "https://play.google.com"
    }
}
