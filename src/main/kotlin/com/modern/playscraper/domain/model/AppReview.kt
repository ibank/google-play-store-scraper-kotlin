package com.modern.playscraper.domain.model

import java.time.OffsetDateTime

/**
 * App review information
 *
 * @property reviewId Review unique ID
 * @property url Review URL
 * @property userName Author username
 * @property userImage Author profile image URL
 * @property date Review date
 * @property score Rating (1-5)
 * @property scoreText Rating text
 * @property title Review title
 * @property text Review content
 * @property replyDate Developer reply date
 * @property replyText Developer reply content
 * @property version App version at time of review
 * @property thumbsUpCount Number of thumbs up
 * @property criteriaId Evaluation criteria ID
 */
data class AppReview(
    val reviewId: String,
    val url: String? = null,
    val userName: String,
    val userImage: String? = null,
    val date: OffsetDateTime,
    val score: Int,
    val scoreText: String? = null,
    val title: String? = null,
    val text: String,
    val replyDate: OffsetDateTime? = null,
    val replyText: String? = null,
    val version: String? = null,
    val thumbsUpCount: Long = 0,
    val criteriaId: Long? = null
)
