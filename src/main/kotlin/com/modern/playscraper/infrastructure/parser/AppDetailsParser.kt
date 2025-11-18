package com.modern.playscraper.infrastructure.parser

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.modern.playscraper.domain.model.AppDetails
import com.modern.playscraper.infrastructure.spec.AppDetailsSpec
import com.modern.playscraper.infrastructure.spec.SpecsRepository
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * App details parser
 *
 * Converts JSON data extracted from HTML into the AppDetails model.
 */
class AppDetailsParser(
    private val specsRepository: SpecsRepository,
    private val scriptDataParser: ScriptDataParser,
    private val jsonPathProcessor: JsonPathProcessor
) {

    /**
     * Parse app details from HTML
     *
     * @param appId App ID
     * @param htmlResponse HTML response
     * @return AppDetails
     */
    fun parse(appId: String, htmlResponse: String): AppDetails {
        // Extract script data from HTML
        val scriptData = scriptDataParser.parse(htmlResponse)

        // Load spec
        val spec = specsRepository.getAppDetailsSpec()

        // Extract data source
        val dataSource = scriptData.get(spec.dataSource) ?: JsonObject()

        return parseFromJson(appId, dataSource, spec)
    }

    /**
     * Parse AppDetails from JsonElement
     */
    private fun parseFromJson(appId: String, dataSource: JsonElement, spec: AppDetailsSpec): AppDetails {
        return AppDetails(
            // Basic information
            appId = appId,
            url = "https://play.google.com/store/apps/details?id=$appId",
            title = extractString(dataSource, spec.title) ?: "",
            descriptionHtml = extractString(dataSource, spec.description) ?: "",
            summary = extractString(dataSource, spec.summary) ?: "",

            // Installs and ratings
            installs = extractString(dataSource, spec.installs) ?: "0",
            minInstalls = extractLong(dataSource, spec.minInstalls) ?: 0,
            maxInstalls = extractLong(dataSource, spec.maxInstalls) ?: 0,
            score = extractDouble(dataSource, spec.score),
            scoreText = extractDouble(dataSource, spec.score)?.toString(),
            ratings = extractLong(dataSource, spec.ratings),
            reviews = extractLong(dataSource, spec.reviews),
            histogram = parseHistogram(dataSource, spec.histogram),

            // Price
            price = extractPrice(dataSource, spec.price),
            priceText = extractPriceText(dataSource, spec.price, spec.isFree),
            currency = extractString(dataSource, spec.currency),
            isFree = extractBoolean(dataSource, spec.isFree) ?: true,
            offersIAP = extractBoolean(dataSource, spec.offersIAP) ?: false,
            IAPRange = extractString(dataSource, spec.IAPRange),

            // Media
            iconUrl = extractString(dataSource, spec.icon) ?: "",
            headerImage = extractString(dataSource, spec.headerImage),
            screenshots = extractScreenshots(dataSource, spec.screenshots),
            video = extractString(dataSource, spec.video),
            videoImage = extractString(dataSource, spec.videoImage),

            // Developer
            developer = extractString(dataSource, spec.developer) ?: "",
            developerId = extractString(dataSource, spec.developerId),
            developerEmail = extractString(dataSource, spec.developerEmail),
            developerWebsite = extractString(dataSource, spec.developerWebsite),
            developerAddress = extractString(dataSource, spec.developerAddress),

            // Technical information
            genre = extractString(dataSource, spec.genre),
            genreId = extractString(dataSource, spec.genreId),
            familyGenre = extractString(dataSource, spec.familyGenre),
            familyGenreId = extractString(dataSource, spec.familyGenreId),
            appSize = extractString(dataSource, spec.appSize),
            androidVersion = extractString(dataSource, spec.androidVersion),
            contentRating = extractString(dataSource, spec.contentRating),
            contentRatingDescription = extractString(dataSource, spec.contentRatingDescription),
            adSupported = extractBoolean(dataSource, spec.adSupported) ?: false,
            containsAds = extractBoolean(dataSource, spec.containsAds) ?: false,

            // Update information
            releaseDate = extractDate(dataSource, spec.releaseDate),
            lastUpdateTimestamp = extractTimestamp(dataSource, spec.lastUpdateTimestamp),
            currentVersion = extractString(dataSource, spec.currentVersion),
            recentChanges = extractString(dataSource, spec.recentChanges),

            // Other
            privacyPolicy = extractString(dataSource, spec.privacyPolicy),
            similarApps = extractSimilarApps(dataSource, spec.similarApps)
        )
    }

    // Helper methods

    private fun extractString(dataSource: JsonElement, pathSpec: com.modern.playscraper.infrastructure.spec.PathSpec): String? {
        return jsonPathProcessor.extractString(dataSource, *pathSpec.toVarargArrayWithoutDataSource())
    }

    private fun extractLong(dataSource: JsonElement, pathSpec: com.modern.playscraper.infrastructure.spec.PathSpec): Long? {
        return jsonPathProcessor.extractLong(dataSource, *pathSpec.toVarargArrayWithoutDataSource())
    }

    private fun extractDouble(dataSource: JsonElement, pathSpec: com.modern.playscraper.infrastructure.spec.PathSpec): Double? {
        return jsonPathProcessor.extractDouble(dataSource, *pathSpec.toVarargArrayWithoutDataSource())
    }

    private fun extractBoolean(dataSource: JsonElement, pathSpec: com.modern.playscraper.infrastructure.spec.PathSpec): Boolean? {
        return jsonPathProcessor.extractBoolean(dataSource, *pathSpec.toVarargArrayWithoutDataSource())
    }

    private fun extractArray(dataSource: JsonElement, pathSpec: com.modern.playscraper.infrastructure.spec.PathSpec): List<JsonElement>? {
        return jsonPathProcessor.extractArray(dataSource, *pathSpec.toVarargArrayWithoutDataSource())
    }

    private fun parseHistogram(dataSource: JsonElement, histogramSpec: AppDetailsSpec.HistogramSpec): AppDetails.RatingHistogram? {
        val oneStar = extractLong(dataSource, histogramSpec.oneStar) ?: return null
        val twoStar = extractLong(dataSource, histogramSpec.twoStar) ?: return null
        val threeStar = extractLong(dataSource, histogramSpec.threeStar) ?: return null
        val fourStar = extractLong(dataSource, histogramSpec.fourStar) ?: return null
        val fiveStar = extractLong(dataSource, histogramSpec.fiveStar) ?: return null

        return AppDetails.RatingHistogram(
            oneStar = oneStar,
            twoStar = twoStar,
            threeStar = threeStar,
            fourStar = fourStar,
            fiveStar = fiveStar
        )
    }

    private fun extractPrice(dataSource: JsonElement, priceSpec: com.modern.playscraper.infrastructure.spec.PathSpec): Double? {
        return extractDouble(dataSource, priceSpec)
    }

    private fun extractPriceText(
        dataSource: JsonElement,
        priceSpec: com.modern.playscraper.infrastructure.spec.PathSpec,
        isFreeSpec: com.modern.playscraper.infrastructure.spec.PathSpec
    ): String? {
        val isFree = extractBoolean(dataSource, isFreeSpec) ?: true
        return if (isFree) {
            "Free"
        } else {
            extractString(dataSource, priceSpec)
        }
    }

    private fun extractScreenshots(dataSource: JsonElement, screenshotsSpec: com.modern.playscraper.infrastructure.spec.PathSpec): List<String> {
        val screenshotsArray = extractArray(dataSource, screenshotsSpec) ?: return emptyList()

        return screenshotsArray.mapNotNull { screenshotElement ->
            // Extract image URL from each screenshot array element
            jsonPathProcessor.extractString(screenshotElement, 3, 2)
        }
    }

    private fun extractSimilarApps(dataSource: JsonElement, similarAppsSpec: com.modern.playscraper.infrastructure.spec.PathSpec): List<String> {
        val appsArray = extractArray(dataSource, similarAppsSpec) ?: return emptyList()

        return appsArray.mapNotNull { appElement ->
            // Extract appId from each app element
            jsonPathProcessor.extractString(appElement, 12, 0)
        }
    }

    private fun extractDate(dataSource: JsonElement, dateSpec: com.modern.playscraper.infrastructure.spec.PathSpec): LocalDate? {
        val dateString = extractString(dataSource, dateSpec) ?: return null

        return try {
            LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            null
        }
    }

    private fun extractTimestamp(dataSource: JsonElement, timestampSpec: com.modern.playscraper.infrastructure.spec.PathSpec): OffsetDateTime? {
        val timestamp = extractLong(dataSource, timestampSpec) ?: return null

        return try {
            OffsetDateTime.ofInstant(
                java.time.Instant.ofEpochSecond(timestamp),
                java.time.ZoneOffset.UTC
            )
        } catch (e: Exception) {
            null
        }
    }
}
