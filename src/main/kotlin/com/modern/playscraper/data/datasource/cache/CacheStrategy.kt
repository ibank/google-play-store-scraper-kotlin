package com.modern.playscraper.data.datasource.cache

/**
 * Caching strategy interface
 */
interface CacheStrategy {
    /**
     * Retrieve data from cache
     *
     * @param key Cache key
     * @return Cached data or null
     */
    suspend fun <T> get(key: String): T?

    /**
     * Store data in cache
     *
     * @param key Cache key
     * @param value Data to store
     * @param ttlMs Time-To-Live (milliseconds)
     */
    suspend fun <T> put(key: String, value: T, ttlMs: Long)

    /**
     * Remove data from cache
     *
     * @param key Cache key
     */
    suspend fun remove(key: String)

    /**
     * Remove all cache
     */
    suspend fun clear()

    /**
     * Remove keys matching specific pattern
     *
     * @param pattern Key pattern (e.g., "app_*")
     */
    suspend fun clearPattern(pattern: String)
}

/**
 * No caching
 */
object NoCaching : CacheStrategy {
    override suspend fun <T> get(key: String): T? = null
    override suspend fun <T> put(key: String, value: T, ttlMs: Long) {}
    override suspend fun remove(key: String) {}
    override suspend fun clear() {}
    override suspend fun clearPattern(pattern: String) {}
}
