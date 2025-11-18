package com.modern.playscraper.infrastructure.parser

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.util.regex.Pattern

/**
 * Parser for extracting JavaScript data from Google Play HTML
 *
 * Google Play pages contain JSON data wrapped in AF_initDataCallback functions.
 * This parser uses regular expressions to extract that data.
 */
class ScriptDataParser {

    private val gson = Gson()

    /**
     * Extract all script data from HTML and return as JsonObject
     *
     * @param rawHtmlResponse HTML response string
     * @return JsonObject containing extracted data (key: "ds:*", value: JSON data)
     */
    fun parse(rawHtmlResponse: String): JsonObject {
        val outObject = JsonObject()
        val scriptMatcher = PATTERN_SCRIPT.matcher(rawHtmlResponse)

        while (scriptMatcher.find()) {
            val matchedData = scriptMatcher.group()
            val keyMatcher = PATTERN_KEY.matcher(matchedData)
            val valueMatcher = PATTERN_VALUE.matcher(matchedData)

            if (keyMatcher.find() && valueMatcher.find()) {
                try {
                    val key = keyMatcher.group(1)  // e.g., "ds:4"
                    val value = valueMatcher.group(1)  // JSON data

                    val jsonValue = gson.fromJson(value, JsonElement::class.java)
                    outObject.add(key, jsonValue)
                } catch (e: Exception) {
                    // Skip data that fails to parse
                    continue
                }
            }
        }

        return outObject
    }

    /**
     * Extract data for specific key only
     *
     * @param rawHtmlResponse HTML response string
     * @param targetKey Key to extract (e.g., "ds:4")
     * @return Extracted JsonElement or null
     */
    fun parseSpecificKey(rawHtmlResponse: String, targetKey: String): JsonElement? {
        val scriptMatcher = PATTERN_SCRIPT.matcher(rawHtmlResponse)

        while (scriptMatcher.find()) {
            val matchedData = scriptMatcher.group()
            val keyMatcher = PATTERN_KEY.matcher(matchedData)
            val valueMatcher = PATTERN_VALUE.matcher(matchedData)

            if (keyMatcher.find() && valueMatcher.find()) {
                val key = keyMatcher.group(1)
                if (key == targetKey) {
                    try {
                        val value = valueMatcher.group(1)
                        return gson.fromJson(value, JsonElement::class.java)
                    } catch (e: Exception) {
                        return null
                    }
                }
            }
        }

        return null
    }

    companion object {
        /**
         * Pattern for finding AF_initDataCallback script blocks
         */
        private val PATTERN_SCRIPT: Pattern = Pattern.compile(
            ">AF_initDataCallback[\\s\\S]*?</script",
            Pattern.MULTILINE
        )

        /**
         * Pattern for extracting data key (e.g., 'ds:4')
         */
        private val PATTERN_KEY: Pattern = Pattern.compile("'(ds:.*?)'")

        /**
         * Pattern for extracting JSON data value
         */
        private val PATTERN_VALUE: Pattern = Pattern.compile(
            "data:([\\s\\S]*?), sideChannel: \\{\\}\\}\\);</"
        )
    }
}
