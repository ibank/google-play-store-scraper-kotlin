package com.example

import com.modern.playscraper.PlayStoreScraper
import com.modern.playscraper.domain.model.AppDetails
import com.modern.playscraper.domain.model.AppReview
import com.modern.playscraper.domain.model.ReviewSortOrder
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking

/**
 * Example consumer application demonstrating Google Play Store Scraper library usage
 * from Maven Central.
 *
 * This example shows:
 * 1. How to add the library as a Maven dependency
 * 2. How to initialize the scraper
 * 3. How to fetch app details and reviews
 * 4. How to handle results using Kotlin Flow
 */
fun main() = runBlocking {
    println("=" * 80)
    println("Google Play Store Scraper - Maven Central Integration Test")
    println("=" * 80)
    println()

    // Configuration from environment variables
    val config = TestConfig(
        appId = System.getenv("TEST_APP_ID") ?: "com.example.testapp",
        language = System.getenv("TEST_LANGUAGE") ?: "en",
        country = System.getenv("TEST_COUNTRY") ?: "us",
        maxReviews = System.getenv("TEST_MAX_REVIEWS")?.toIntOrNull() ?: 5
    )

    println("Configuration:")
    println("  App ID: ${config.appId}")
    println("  Language: ${config.language}")
    println("  Country: ${config.country}")
    println("  Max Reviews: ${config.maxReviews}")
    println()
    println("  To customize, set environment variables:")
    println("  - TEST_APP_ID (default: com.example.testapp)")
    println("  - TEST_LANGUAGE (default: en)")
    println("  - TEST_COUNTRY (default: us)")
    println("  - TEST_MAX_REVIEWS (default: 5)")
    println()

    // Initialize the scraper
    val scraper = PlayStoreScraper()

    try {
        // Example 1: Fetch app details
        println("📱 Example 1: Fetching App Details")
        println("-" * 80)
        testAppDetails(scraper, config)
        println()

        // Example 2: Fetch reviews
        println("⭐ Example 2: Fetching Reviews")
        println("-" * 80)
        testReviews(scraper, config)
        println()

        println("✅ All tests completed successfully!")
        println("=" * 80)

    } catch (e: Exception) {
        println("❌ Error: ${e.message}")
        e.printStackTrace()
    } finally {
        scraper.close()
    }
}

/**
 * Test configuration
 */
data class TestConfig(
    val appId: String,
    val language: String,
    val country: String,
    val maxReviews: Int
)

/**
 * Test app details fetching functionality
 */
suspend fun testAppDetails(scraper: PlayStoreScraper, config: TestConfig) {
    println("Fetching details for: ${config.appId}")
    println()

    val result = scraper.getAppDetails(
        appId = config.appId,
        language = config.language,
        country = config.country
    )
    result.fold(
        onSuccess = { app ->
            displayAppDetails(app)
        },
        onFailure = { error ->
            println("❌ Failed to fetch app details: ${error.message}")
            error.printStackTrace()
        }
    )
}

/**
 * Display app details in a formatted way
 */
fun displayAppDetails(app: AppDetails) {
    println("Title: ${app.title}")
    println("Developer: ${app.developer}")
    println("Rating: ${app.score ?: "N/A"}")
    println("Installs: ${app.installs}")
    println("Price: ${if (app.isFree) "Free" else "${app.priceText ?: "$${app.price}"}"}")
    println("Category: ${app.genre ?: "Unknown"}")
    println("Version: ${app.currentVersion ?: "Unknown"}")
    println("Updated: ${app.lastUpdateDate ?: "Unknown"}")
    println()
    println("Description (first 200 chars):")
    println(app.summary.take(200) + if (app.summary.length > 200) "..." else "")
    println()
}

/**
 * Test reviews fetching functionality
 */
suspend fun testReviews(scraper: PlayStoreScraper, config: TestConfig) {
    println("Fetching up to ${config.maxReviews} reviews for: ${config.appId}")
    println()

    var count = 0
    scraper.getAppReviewsFlow(
        appId = config.appId,
        sortOrder = ReviewSortOrder.NEWEST,
        language = config.language,
        country = config.country,
        limit = config.maxReviews
    )
        .take(config.maxReviews)
        .collect { review ->
            count++
            displayReview(count, review)
        }

    println("Found $count reviews")
}

/**
 * Display a single review in a formatted way
 */
fun displayReview(index: Int, review: AppReview) {
    println("Review #$index")
    println("  Author: ${review.userName}")
    println("  Rating: ${"⭐".repeat(review.score)}")
    println("  Date: ${review.date}")

    review.title?.takeIf { it.isNotBlank() }?.let {
        println("  Title: $it")
    }

    println("  Text: ${review.text.take(100)}${if (review.text.length > 100) "..." else ""}")

    review.replyText?.takeIf { it.isNotBlank() }?.let { replyText ->
        println("  Developer Reply: ${replyText.take(100)}${if (replyText.length > 100) "..." else ""}")
        println("  Reply Date: ${review.replyDate}")
    }

    println("  Thumbs Up: ${review.thumbsUpCount}")
    println()
}

/**
 * String multiplication operator for creating separator lines
 */
private operator fun String.times(count: Int): String = this.repeat(count)
