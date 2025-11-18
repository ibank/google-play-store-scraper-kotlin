package com.modern.playscraper.domain.model

/**
 * Basic app information
 *
 * @property appId App package name (e.g., com.example.app)
 * @property title App title
 * @property summary App summary description
 * @property score Average rating (1.0 ~ 5.0)
 * @property scoreText Rating text representation
 * @property url Google Play Store URL
 * @property iconUrl App icon image URL
 * @property developer Developer name
 * @property priceText Price text (e.g., "₩1,200" or "Free")
 * @property price Price (numeric)
 * @property currency Currency code (e.g., "KRW", "USD")
 * @property isFree Whether the app is free
 */
data class App(
    val appId: String,
    val title: String,
    val summary: String,
    val score: Double? = null,
    val scoreText: String? = null,
    val url: String,
    val iconUrl: String,
    val developer: String,
    val priceText: String? = null,
    val price: Double? = null,
    val currency: String? = null,
    val isFree: Boolean = true
)
