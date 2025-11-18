package com.modern.playscraper.domain.model

/**
 * Google Play Store app collection types
 */
enum class Collection(val id: String) {
    /** Top free apps */
    TOP_FREE("topselling_free"),

    /** Top paid apps */
    TOP_PAID("topselling_paid"),

    /** Top grossing apps */
    GROSSING("topgrossing"),

    /** New free apps */
    NEW_FREE("topselling_new_free"),

    /** New paid apps */
    NEW_PAID("topselling_new_paid");

    companion object {
        fun fromId(id: String): Collection? = entries.find { it.id == id }
    }
}
