package com.modern.playscraper.infrastructure.parser

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import com.jayway.jsonpath.spi.json.GsonJsonProvider
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider

/**
 * Processor for extracting desired values from JSON data using JsonPath
 */
class JsonPathProcessor {

    @PublishedApi
    internal val gson = Gson()

    @PublishedApi
    internal val jsonPathConfig: Configuration = Configuration.builder()
        .jsonProvider(GsonJsonProvider(gson))
        .mappingProvider(GsonMappingProvider(gson))
        .options(Option.SUPPRESS_EXCEPTIONS, Option.DEFAULT_PATH_LEAF_TO_NULL)
        .build()

    /**
     * Extract data from JSON string by path
     *
     * @param jsonString JSON string
     * @param path JsonPath path (e.g., "$.store.book[0].title")
     * @return Extracted value or null
     */
    inline fun <reified T> extract(jsonString: String, path: String): T? {
        return try {
            JsonPath.using(jsonPathConfig).parse(jsonString).read(path, T::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Extract data from JsonElement by path
     *
     * @param jsonElement JsonElement
     * @param path JsonPath path
     * @return Extracted value or null
     */
    inline fun <reified T> extract(jsonElement: JsonElement, path: String): T? {
        return try {
            val jsonString = gson.toJson(jsonElement)
            JsonPath.using(jsonPathConfig).parse(jsonString).read(path, T::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Extract data from JsonElement by array index-based path
     *
     * @param jsonElement JsonElement
     * @param pathElements Path elements (string keys or integer indices)
     * @return Extracted JsonElement or null
     */
    fun extractByPath(jsonElement: JsonElement, vararg pathElements: Any): JsonElement? {
        var current: JsonElement? = jsonElement

        for (element in pathElements) {
            current = when {
                current == null || current.isJsonNull -> return null
                element is String && current.isJsonObject -> {
                    current.asJsonObject.get(element)
                }
                element is Int && current.isJsonArray -> {
                    val array = current.asJsonArray
                    if (element >= 0 && element < array.size()) {
                        array[element]
                    } else {
                        null
                    }
                }
                else -> null
            }
        }

        return current
    }

    /**
     * Extract string from JsonElement by path
     */
    fun extractString(jsonElement: JsonElement, vararg pathElements: Any): String? {
        val element = extractByPath(jsonElement, *pathElements)
        return when {
            element == null || element.isJsonNull -> null
            element.isJsonPrimitive -> element.asString
            else -> null
        }
    }

    /**
     * Extract Long from JsonElement by path
     */
    fun extractLong(jsonElement: JsonElement, vararg pathElements: Any): Long? {
        val element = extractByPath(jsonElement, *pathElements)
        return when {
            element == null || element.isJsonNull -> null
            element.isJsonPrimitive && element.asJsonPrimitive.isNumber -> {
                element.asLong
            }
            else -> null
        }
    }

    /**
     * Extract Double from JsonElement by path
     */
    fun extractDouble(jsonElement: JsonElement, vararg pathElements: Any): Double? {
        val element = extractByPath(jsonElement, *pathElements)
        return when {
            element == null || element.isJsonNull -> null
            element.isJsonPrimitive && element.asJsonPrimitive.isNumber -> {
                element.asDouble
            }
            else -> null
        }
    }

    /**
     * Extract Boolean from JsonElement by path
     */
    fun extractBoolean(jsonElement: JsonElement, vararg pathElements: Any): Boolean? {
        val element = extractByPath(jsonElement, *pathElements)
        return when {
            element == null || element.isJsonNull -> null
            element.isJsonPrimitive && element.asJsonPrimitive.isBoolean -> {
                element.asBoolean
            }
            else -> null
        }
    }

    /**
     * Extract array from JsonElement by path
     */
    fun extractArray(jsonElement: JsonElement, vararg pathElements: Any): List<JsonElement>? {
        val element = extractByPath(jsonElement, *pathElements)
        return when {
            element == null || element.isJsonNull -> null
            element.isJsonArray -> element.asJsonArray.toList()
            else -> null
        }
    }
}
