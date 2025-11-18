package com.modern.playscraper.infrastructure.parser

import com.google.gson.JsonElement
import com.modern.playscraper.domain.model.AppReview
import java.time.OffsetDateTime

/**
 * App reviews parser
 */
class AppReviewsParser(
    private val scriptDataParser: ScriptDataParser,
    private val jsonPathProcessor: JsonPathProcessor
) {

    /**
     * Parse app review list from HTML
     *
     * @param htmlResponse HTML response
     * @return List<AppReview>
     */
    fun parse(htmlResponse: String): List<AppReview> {
        // Extract script data from HTML
        val scriptData = scriptDataParser.parse(htmlResponse)

        // Extract data source (reviews are in ds:11 - Google Play structure changed)
        val dataSource = scriptData.get("ds:11") ?: return emptyList()

        // Extract reviews array
        val reviewsArray = jsonPathProcessor.extractArray(dataSource, 0)
            ?: return emptyList()

        // Convert each review element to AppReview model
        return reviewsArray.mapNotNull { reviewElement ->
            parseReview(reviewElement)
        }
    }

    /**
     * Parse AppReview from JsonElement
     */
    private fun parseReview(reviewElement: JsonElement): AppReview? {
        return try {
            val reviewId = jsonPathProcessor.extractString(reviewElement, 0) ?: return null
            val userName = jsonPathProcessor.extractString(reviewElement, 1, 0) ?: return null
            val userImage = jsonPathProcessor.extractString(reviewElement, 1, 1, 3, 2)

            // Timestamp (in seconds)
            val timestampSeconds = jsonPathProcessor.extractLong(reviewElement, 5, 0) ?: return null
            val date = OffsetDateTime.ofInstant(
                java.time.Instant.ofEpochSecond(timestampSeconds),
                java.time.ZoneOffset.UTC
            )

            val score = jsonPathProcessor.extractDouble(reviewElement, 2)?.toInt() ?: return null
            val scoreText = "$score"
            val text = jsonPathProcessor.extractString(reviewElement, 4) ?: ""
            val title = jsonPathProcessor.extractString(reviewElement, 3)

            // Developer reply
            val replyElement = jsonPathProcessor.extractByPath(reviewElement, 7)
            val replyText = replyElement?.let {
                jsonPathProcessor.extractString(it, 1)
            }
            val replyTimestampSeconds = replyElement?.let {
                jsonPathProcessor.extractLong(it, 2, 0)
            }
            val replyDate = replyTimestampSeconds?.let {
                OffsetDateTime.ofInstant(
                    java.time.Instant.ofEpochSecond(it),
                    java.time.ZoneOffset.UTC
                )
            }

            val version = jsonPathProcessor.extractString(reviewElement, 10)
            val thumbsUpCount = jsonPathProcessor.extractLong(reviewElement, 6) ?: 0
            val criteriaId = jsonPathProcessor.extractLong(reviewElement, 14)

            val url = "https://play.google.com/store/apps/details?reviewId=$reviewId"

            AppReview(
                reviewId = reviewId,
                url = url,
                userName = userName,
                userImage = userImage,
                date = date,
                score = score,
                scoreText = scoreText,
                title = title,
                text = text,
                replyDate = replyDate,
                replyText = replyText,
                version = version,
                thumbsUpCount = thumbsUpCount,
                criteriaId = criteriaId
            )
        } catch (e: Exception) {
            null
        }
    }
}
