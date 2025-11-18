package com.modern.playscraper.infrastructure.http

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

/**
 * Ktor HTTP client factory
 */
object KtorClientFactory {

    /**
     * Create HTTP client
     *
     * @param throttler Request throttler
     * @param userAgentProvider User-Agent provider
     * @param connectTimeoutMs Connection timeout (milliseconds)
     * @param requestTimeoutMs Request timeout (milliseconds)
     * @param enableLogging Enable logging
     * @param minimalHeaders Use standard HTTP headers with User-Agent only
     */
    fun create(
        throttler: RequestThrottler = HumanBehaviorRequestThrottler(),
        userAgentProvider: UserAgentProvider = DefaultUserAgentProvider(),
        connectTimeoutMs: Long = 60_000,
        requestTimeoutMs: Long = 60_000,
        enableLogging: Boolean = false,
        minimalHeaders: Boolean = false
    ): HttpClient {
        return HttpClient(OkHttp) {
            // OkHttp engine configuration
            engine {
                config {
                    connectTimeout(connectTimeoutMs, TimeUnit.MILLISECONDS)
                    readTimeout(requestTimeoutMs, TimeUnit.MILLISECONDS)
                    writeTimeout(requestTimeoutMs, TimeUnit.MILLISECONDS)

                    // Connection pool configuration
                    connectionPool(
                        okhttp3.ConnectionPool(5, 5, TimeUnit.MINUTES)
                    )

                    // Retry on connection failure
                    retryOnConnectionFailure(true)
                }
            }

            // Content Negotiation (JSON)
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = false
                })
            }

            // Default Request configuration
            install(DefaultRequest) {
                // Add User-Agent header
                header(HttpHeaders.UserAgent, userAgentProvider.provide())

                // Set additional headers only if not in minimal headers mode
                if (!minimalHeaders) {
                    // Basic headers
                    header(HttpHeaders.Accept, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    header(HttpHeaders.AcceptLanguage, "en-US,en;q=0.9")
                    header(HttpHeaders.AcceptEncoding, "gzip, deflate, br")
                    header(HttpHeaders.CacheControl, "no-cache")
                    header("DNT", "1")
                    header("Upgrade-Insecure-Requests", "1")
                }
            }

            // HTTP Timeout configuration
            install(HttpTimeout) {
                requestTimeoutMillis = requestTimeoutMs
                connectTimeoutMillis = connectTimeoutMs
                socketTimeoutMillis = requestTimeoutMs
            }

            // Logging (optional)
            if (enableLogging) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.INFO
                }
            }

            // Request Interceptor (Throttling)
            install(createThrottlerPlugin(throttler))
        }
    }

    /**
     * Create throttler plugin
     */
    private fun createThrottlerPlugin(throttler: RequestThrottler) = io.ktor.client.plugins.api.createClientPlugin("RequestThrottler") {
        on(io.ktor.client.plugins.api.Send) { request ->
            throttler.awaitContinue()
            proceed(request)
        }
    }
}
