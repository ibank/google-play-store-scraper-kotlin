package com.modern.playscraper.data.datasource.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

/**
 * Memory-based cache implementation
 *
 * Thread-safe in-memory cache.
 */
class InMemoryCache : CacheStrategy {

    private val cache = ConcurrentHashMap<String, CacheEntry<*>>()
    private val mutex = Mutex()

    /**
     * Cache entry (value + expiration time)
     */
    private data class CacheEntry<T>(
        val value: T,
        val expiresAt: Long
    ) {
        fun isExpired(): Boolean = System.currentTimeMillis() >= expiresAt
    }

    override suspend fun <T> get(key: String): T? = mutex.withLock {
        @Suppress("UNCHECKED_CAST")
        val entry = cache[key] as? CacheEntry<T> ?: return null

        if (entry.isExpired()) {
            cache.remove(key)
            return null
        }

        return entry.value
    }

    override suspend fun <T> put(key: String, value: T, ttlMs: Long) = mutex.withLock {
        val expiresAt = System.currentTimeMillis() + ttlMs
        cache[key] = CacheEntry(value, expiresAt)
    }

    override suspend fun remove(key: String): Unit = mutex.withLock {
        cache.remove(key)
        Unit
    }

    override suspend fun clear() = mutex.withLock {
        cache.clear()
    }

    override suspend fun clearPattern(pattern: String) = mutex.withLock {
        val regex = pattern.replace("*", ".*").toRegex()
        val keysToRemove = cache.keys.filter { regex.matches(it) }
        keysToRemove.forEach { cache.remove(it) }
    }

    /**
     * Clean up expired entries
     */
    suspend fun cleanupExpired() = mutex.withLock {
        val now = System.currentTimeMillis()
        val expiredKeys = cache.entries
            .filter { (_, entry) -> entry.expiresAt <= now }
            .map { it.key }

        expiredKeys.forEach { cache.remove(it) }
    }

    /**
     * Get cache size
     */
    fun size(): Int = cache.size
}
