package com.modern.playscraper.infrastructure.spec

import kotlinx.serialization.Serializable

/**
 * App details specification
 *
 * Defines JSON paths for extracting app details from Google Play.
 */
@Serializable
data class AppDetailsSpec(
    val version: String = "1.0",
    val dataSource: String = "ds:5",

    // Basic information
    val title: PathSpec,
    val description: PathSpec,
    val summary: PathSpec,

    // Installs and ratings
    val installs: PathSpec,
    val minInstalls: PathSpec,
    val maxInstalls: PathSpec,
    val score: PathSpec,
    val ratings: PathSpec,
    val reviews: PathSpec,
    val histogram: HistogramSpec,

    // Price
    val price: PathSpec,
    val isFree: PathSpec,
    val currency: PathSpec,
    val offersIAP: PathSpec,
    val IAPRange: PathSpec,

    // Media
    val icon: PathSpec,
    val headerImage: PathSpec,
    val screenshots: PathSpec,
    val video: PathSpec,
    val videoImage: PathSpec,

    // Developer
    val developer: PathSpec,
    val developerId: PathSpec,
    val developerEmail: PathSpec,
    val developerWebsite: PathSpec,
    val developerAddress: PathSpec,

    // Technical information
    val genre: PathSpec,
    val genreId: PathSpec,
    val familyGenre: PathSpec,
    val familyGenreId: PathSpec,
    val appSize: PathSpec,
    val androidVersion: PathSpec,
    val contentRating: PathSpec,
    val contentRatingDescription: PathSpec,
    val adSupported: PathSpec,
    val containsAds: PathSpec,

    // Update information
    val releaseDate: PathSpec,
    val lastUpdateTimestamp: PathSpec,
    val currentVersion: PathSpec,
    val recentChanges: PathSpec,

    // Other
    val privacyPolicy: PathSpec,
    val similarApps: PathSpec
) {
    @Serializable
    data class HistogramSpec(
        val oneStar: PathSpec,
        val twoStar: PathSpec,
        val threeStar: PathSpec,
        val fourStar: PathSpec,
        val fiveStar: PathSpec
    )
}
