package com.modern.playscraper.domain.model

/**
 * App permission information
 *
 * @property type Permission type (e.g., "Location", "Camera", "Contacts")
 * @property description Permission description
 */
data class Permission(
    val type: String,
    val description: String
)
