package com.modern.playscraper.infrastructure.parser

import com.google.gson.JsonElement
import com.modern.playscraper.domain.model.App

/**
 * App list parser
 *
 * Used commonly for developer apps, similar apps, category apps, search results, etc.
 */
class AppsListParser(
    private val scriptDataParser: ScriptDataParser,
    private val jsonPathProcessor: JsonPathProcessor
) {

    /**
     * Parse app list from HTML
     *
     * @param htmlResponse HTML response
     * @param dataSourceKey Data source key (e.g., "ds:3", "ds:4")
     * @param appsArrayPath App array path
     * @return List<App>
     */
    fun parse(
        htmlResponse: String,
        dataSourceKey: String = "ds:3",
        appsArrayPath: List<Any> = listOf(1, 2, 0)
    ): List<App> {
        // Extract script data from HTML
        val scriptData = scriptDataParser.parse(htmlResponse)

        // Extract data source
        val dataSource = scriptData.get(dataSourceKey) ?: return emptyList()

        // Extract app array
        val appsArray = jsonPathProcessor.extractArray(dataSource, *appsArrayPath.toTypedArray())
            ?: return emptyList()

        // Convert each app element to App model
        return appsArray.mapNotNull { appElement ->
            parseApp(appElement)
        }
    }

    /**
     * Parse App from JsonElement
     */
    private fun parseApp(appElement: JsonElement): App? {
        return try {
            val appId = jsonPathProcessor.extractString(appElement, 12, 0) ?: return null
            val title = jsonPathProcessor.extractString(appElement, 2) ?: return null
            val developer = jsonPathProcessor.extractString(appElement, 4, 0) ?: ""
            val iconUrl = jsonPathProcessor.extractString(appElement, 1, 1, 0, 3, 2) ?: ""
            val score = jsonPathProcessor.extractDouble(appElement, 6, 0, 2, 1, 0)
            val scoreText = score?.toString()
            val summary = jsonPathProcessor.extractString(appElement, 4, 1, 1, 1, 1) ?: ""
            val url = "https://play.google.com/store/apps/details?id=$appId"

            // Price information
            val priceElement = jsonPathProcessor.extractByPath(appElement, 7, 0, 3, 2, 1, 0)
            val price = priceElement?.let {
                jsonPathProcessor.extractDouble(it, 0)
            }
            val priceText = priceElement?.let {
                jsonPathProcessor.extractString(it, 2)
            }
            val currency = priceElement?.let {
                jsonPathProcessor.extractString(it, 1)
            }
            val isFree = price == null || price == 0.0

            App(
                appId = appId,
                title = title,
                summary = summary,
                score = score,
                scoreText = scoreText,
                url = url,
                iconUrl = iconUrl,
                developer = developer,
                priceText = priceText,
                price = price,
                currency = currency,
                isFree = isFree
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Parse apps for search results (structure is slightly different)
     */
    fun parseSearchResults(htmlResponse: String): List<App> {
        val scriptData = scriptDataParser.parse(htmlResponse)
        val dataSource = scriptData.get("ds:3") ?: return emptyList()

        // Search results may have different nesting
        val searchResults = jsonPathProcessor.extractArray(dataSource, 0, 1, 0, 0, 0)
            ?: return emptyList()

        return searchResults.mapNotNull { resultElement ->
            parseApp(resultElement)
        }
    }
}
