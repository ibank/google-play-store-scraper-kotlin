package com.modern.playscraper.infrastructure.parser

import com.google.gson.JsonElement
import com.modern.playscraper.domain.model.Permission

/**
 * App permissions parser
 */
class PermissionsParser(
    private val scriptDataParser: ScriptDataParser,
    private val jsonPathProcessor: JsonPathProcessor
) {

    /**
     * Parse app permission list from HTML
     *
     * @param htmlResponse HTML response
     * @return List<Permission>
     */
    fun parse(htmlResponse: String): List<Permission> {
        // Extract script data from HTML
        val scriptData = scriptDataParser.parse(htmlResponse)

        // Extract data source (permissions are usually in ds:3)
        val dataSource = scriptData.get("ds:3") ?: return emptyList()

        // Extract permissions array
        val permissionsArray = jsonPathProcessor.extractArray(dataSource, 2)
            ?: return emptyList()

        // Convert each permission element to Permission model
        return permissionsArray.mapNotNull { permissionElement ->
            parsePermission(permissionElement)
        }
    }

    /**
     * Parse Permission from JsonElement
     */
    private fun parsePermission(permissionElement: JsonElement): Permission? {
        return try {
            val type = jsonPathProcessor.extractString(permissionElement, 0) ?: return null
            val description = jsonPathProcessor.extractString(permissionElement, 1) ?: ""

            Permission(
                type = type,
                description = description
            )
        } catch (e: Exception) {
            null
        }
    }
}
