package com.modern.playscraper.infrastructure.spec

import kotlinx.serialization.Serializable

/**
 * JSON path specification
 *
 * Defines paths for finding specific fields in Google Play's data structure.
 * Example: ["ds:4", 1, 2, 0, 0] → $['ds:4'][1][2][0][0]
 */
@Serializable
data class PathSpec(
    /**
     * Path elements (string keys or integer indices)
     */
    val elements: List<PathElement>
) {
    /**
     * Path element (key or index)
     */
    @Serializable
    sealed class PathElement {
        /**
         * Array index
         */
        @Serializable
        data class Index(val value: Int) : PathElement()

        /**
         * Object key
         */
        @Serializable
        data class Key(val value: String) : PathElement()
    }

    /**
     * Convert to JsonPath string
     */
    fun toJsonPath(): String {
        return elements.joinToString("", prefix = "$") { element ->
            when (element) {
                is PathElement.Index -> "[${element.value}]"
                is PathElement.Key -> "['${element.value}']"
            }
        }
    }

    /**
     * Convert to vararg array (for JsonPathProcessor.extractByPath)
     */
    fun toVarargArray(): Array<Any> {
        return elements.map { element ->
            when (element) {
                is PathElement.Index -> element.value
                is PathElement.Key -> element.value
            }
        }.toTypedArray()
    }

    /**
     * Convert to vararg array excluding first element (data source key)
     */
    fun toVarargArrayWithoutDataSource(): Array<Any> {
        return elements.drop(1).map { element ->
            when (element) {
                is PathElement.Index -> element.value
                is PathElement.Key -> element.value
            }
        }.toTypedArray()
    }

    companion object {
        /**
         * Create PathSpec from variable arguments
         *
         * Example: path("ds:4", 1, 2, 0, 0)
         */
        fun of(vararg elements: Any): PathSpec {
            return PathSpec(
                elements.map { element ->
                    when (element) {
                        is String -> PathElement.Key(element)
                        is Int -> PathElement.Index(element)
                        else -> throw IllegalArgumentException(
                            "Path element must be String or Int, got: ${element::class.simpleName}"
                        )
                    }
                }
            )
        }
    }
}

/**
 * Helper function for creating PathSpec
 */
fun path(vararg elements: Any): PathSpec = PathSpec.of(*elements)
