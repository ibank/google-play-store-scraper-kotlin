package com.modern.playscraper.infrastructure.http

import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * HTTP request throttler interface
 */
interface RequestThrottler {
    /**
     * Wait until next request
     */
    suspend fun awaitContinue()
}

/**
 * No request throttling (fast but risky)
 */
object NoRequestThrottling : RequestThrottler {
    override suspend fun awaitContinue() {
        // No waiting
    }
}

/**
 * Request throttler mimicking human behavior pattern
 *
 * @property baseDelayMs Base wait time (milliseconds)
 * @property jitterMinMs Minimum random delay (milliseconds)
 * @property jitterMaxMs Maximum random delay (milliseconds)
 */
class HumanBehaviorRequestThrottler(
    private val baseDelayMs: Long = DEFAULT_BASE_DELAY_MS,
    private val jitterMinMs: Long = DEFAULT_JITTER_MIN_MS,
    private val jitterMaxMs: Long = DEFAULT_JITTER_MAX_MS
) : RequestThrottler {

    private var lastRequestTimeMs: Long = 0

    override suspend fun awaitContinue() {
        val now = System.currentTimeMillis()
        val elapsed = now - lastRequestTimeMs

        // Calculate random jitter
        val jitter = if (jitterMaxMs > jitterMinMs) {
            Random.nextLong(jitterMinMs, jitterMaxMs)
        } else {
            jitterMinMs
        }

        val totalDelay = baseDelayMs + jitter

        // Wait if elapsed time is less than total delay
        if (elapsed < totalDelay) {
            delay(totalDelay - elapsed)
        }

        lastRequestTimeMs = System.currentTimeMillis()
    }

    companion object {
        const val DEFAULT_BASE_DELAY_MS = 1_000L      // 1 second
        const val DEFAULT_JITTER_MIN_MS = 0L          // 0 seconds
        const val DEFAULT_JITTER_MAX_MS = 1_000L      // 1 second
    }
}

/**
 * Fixed interval request throttler
 *
 * @property intervalMs Request interval (milliseconds)
 */
class FixedIntervalThrottler(
    private val intervalMs: Long
) : RequestThrottler {

    private var lastRequestTimeMs: Long = 0

    override suspend fun awaitContinue() {
        val now = System.currentTimeMillis()
        val elapsed = now - lastRequestTimeMs

        if (elapsed < intervalMs) {
            delay(intervalMs - elapsed)
        }

        lastRequestTimeMs = System.currentTimeMillis()
    }
}
