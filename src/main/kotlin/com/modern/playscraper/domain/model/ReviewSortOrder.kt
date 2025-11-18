package com.modern.playscraper.domain.model

/**
 * Review sort order
 */
enum class ReviewSortOrder(val value: Int) {
    /** Most helpful */
    MOST_HELPFUL(1),

    /** Newest */
    NEWEST(2),

    /** Highest rating */
    RATING(3);

    companion object {
        fun fromValue(value: Int): ReviewSortOrder? = entries.find { it.value == value }
    }
}
