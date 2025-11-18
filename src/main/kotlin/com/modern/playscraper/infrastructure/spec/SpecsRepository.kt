package com.modern.playscraper.infrastructure.spec

/**
 * Specs repository
 *
 * Provides path specifications for extracting data from Google Play pages.
 * Written with reference to the original project's Specs.kt.
 */
class SpecsRepository {

    /**
     * App details specification
     */
    fun getAppDetailsSpec(): AppDetailsSpec {
        return AppDetailsSpec(
            version = "1.0",
            dataSource = "ds:5",

            // Basic information
            title = path("ds:5", 1, 2, 0, 0),
            description = path("ds:5", 1, 2, 72, 0, 1),
            summary = path("ds:5", 1, 2, 73, 0, 1),

            // Installs and ratings
            installs = path("ds:5", 1, 2, 13, 0),
            minInstalls = path("ds:5", 1, 2, 13, 1),
            maxInstalls = path("ds:5", 1, 2, 13, 2),
            score = path("ds:5", 1, 2, 51, 0, 1),
            ratings = path("ds:5", 1, 2, 51, 2, 1),
            reviews = path("ds:5", 1, 2, 51, 3, 1),
            histogram = AppDetailsSpec.HistogramSpec(
                oneStar = path("ds:5", 1, 2, 51, 1, 1, 1),
                twoStar = path("ds:5", 1, 2, 51, 1, 2, 1),
                threeStar = path("ds:5", 1, 2, 51, 1, 3, 1),
                fourStar = path("ds:5", 1, 2, 51, 1, 4, 1),
                fiveStar = path("ds:5", 1, 2, 51, 1, 5, 1)
            ),

            // Price
            price = path("ds:5", 1, 2, 57, 0, 0, 0, 0, 1, 0, 0),
            isFree = path("ds:5", 1, 2, 57, 0),
            currency = path("ds:5", 1, 2, 57, 0, 0, 0, 0, 1, 0, 1),
            offersIAP = path("ds:5", 1, 2, 19, 0),
            IAPRange = path("ds:5", 1, 2, 19, 0),

            // Media
            icon = path("ds:5", 1, 2, 95, 0, 3, 2),
            headerImage = path("ds:5", 1, 2, 96, 0, 3, 2),
            screenshots = path("ds:5", 1, 2, 78, 0),
            video = path("ds:5", 1, 2, 100, 0, 0, 3, 2),
            videoImage = path("ds:5", 1, 2, 100, 1, 0, 3, 2),

            // Developer
            developer = path("ds:5", 1, 2, 68, 0),
            developerId = path("ds:5", 1, 2, 68, 1, 4, 2),
            developerEmail = path("ds:5", 1, 2, 69, 1, 0),
            developerWebsite = path("ds:5", 1, 2, 69, 0, 5, 2),
            developerAddress = path("ds:5", 1, 2, 69, 2, 0),

            // Technical information
            genre = path("ds:5", 1, 2, 79, 0, 0, 0),
            genreId = path("ds:5", 1, 2, 79, 0, 0, 2),
            familyGenre = path("ds:5", 0, 12, 13, 1, 0),
            familyGenreId = path("ds:5", 0, 12, 13, 1, 2),
            appSize = path("ds:8", 0),
            androidVersion = path("ds:5", 1, 2, 140, 1, 1, 0, 0, 1),
            contentRating = path("ds:5", 1, 2, 9, 0),
            contentRatingDescription = path("ds:5", 1, 2, 9, 2, 1),
            adSupported = path("ds:5", 1, 2, 48),
            containsAds = path("ds:5", 1, 2, 48),

            // Update information
            releaseDate = path("ds:5", 1, 2, 10, 0),
            lastUpdateTimestamp = path("ds:5", 1, 2, 145, 0, 1, 0),
            currentVersion = path("ds:5", 1, 2, 140, 0, 0, 0),
            recentChanges = path("ds:5", 1, 2, 144, 1, 1),

            // Other
            privacyPolicy = path("ds:5", 1, 2, 99, 0, 5, 2),
            similarApps = path("ds:5", 1, 2, 64, 0)
        )
    }
}
