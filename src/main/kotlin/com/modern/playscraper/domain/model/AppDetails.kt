package com.modern.playscraper.domain.model

import java.time.LocalDate
import java.time.OffsetDateTime

/**
 * App details information (44 fields)
 *
 * Contains all major information provided by Google Play Store.
 */
data class AppDetails(
    // Basic information
    val appId: String,
    val url: String,
    val title: String,
    val descriptionHtml: String,
    val summary: String,

    // Installs and ratings
    val installs: String,
    val minInstalls: Long,
    val maxInstalls: Long,
    val score: Double? = null,
    val scoreText: String? = null,
    val ratings: Long? = null,
    val reviews: Long? = null,
    val histogram: RatingHistogram? = null,

    // Price
    val price: Double? = null,
    val priceText: String? = null,
    val currency: String? = null,
    val isFree: Boolean = true,
    val offersIAP: Boolean = false,
    val IAPRange: String? = null,

    // Media
    val iconUrl: String,
    val headerImage: String? = null,
    val screenshots: List<String> = emptyList(),
    val video: String? = null,
    val videoImage: String? = null,

    // Developer
    val developer: String,
    val developerId: String? = null,
    val developerEmail: String? = null,
    val developerWebsite: String? = null,
    val developerAddress: String? = null,
    val developerInternalID: String? = null,

    // Technical information
    val genre: String? = null,
    val genreId: String? = null,
    val familyGenre: String? = null,
    val familyGenreId: String? = null,
    val categories: List<String> = emptyList(),
    val appSize: String? = null,
    val androidVersion: String? = null,
    val androidVersionMin: String? = null,
    val androidVersionText: String? = null,
    val contentRating: String? = null,
    val contentRatingDescription: String? = null,
    val adSupported: Boolean = false,
    val containsAds: Boolean = false,

    // Update information
    val releaseDate: LocalDate? = null,
    val lastUpdateTimestamp: OffsetDateTime? = null,
    val lastUpdateDate: String? = null,
    val currentVersion: String? = null,
    val currentVersionCode: Long? = null,
    val recentChanges: String? = null,
    val recentChangesHTML: String? = null,

    // Other
    val privacyPolicy: String? = null,
    val isEditorsChoice: Boolean = false,
    val similarApps: List<String> = emptyList()
) {
    /**
     * Rating histogram (count by star rating)
     */
    data class RatingHistogram(
        val oneStar: Long = 0,
        val twoStar: Long = 0,
        val threeStar: Long = 0,
        val fourStar: Long = 0,
        val fiveStar: Long = 0
    ) {
        val total: Long
            get() = oneStar + twoStar + threeStar + fourStar + fiveStar
    }
}
