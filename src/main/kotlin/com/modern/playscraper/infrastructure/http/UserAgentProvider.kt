package com.modern.playscraper.infrastructure.http

import kotlin.random.Random

/**
 * User-Agent provider interface
 */
interface UserAgentProvider {
    /**
     * Provide User-Agent string
     */
    fun provide(): String
}

/**
 * Default User-Agent provider
 */
class DefaultUserAgentProvider : UserAgentProvider {
    override fun provide(): String {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    }
}

/**
 * Load and provide User-Agent list from resource file
 *
 * @property resourcePath Resource file path
 * @property random Random selection (true: random, false: sequential)
 */
class ResourceFileUserAgentProvider(
    private val resourcePath: String = "user_agents.txt",
    private val random: Boolean = true
) : UserAgentProvider {

    private val userAgents: List<String> by lazy {
        loadUserAgents()
    }

    private var currentIndex = 0

    private fun loadUserAgents(): List<String> {
        return try {
            val resource = this::class.java.classLoader.getResourceAsStream(resourcePath)
            resource?.bufferedReader()?.use { reader ->
                reader.readLines()
                    .map { it.trim() }
                    .filter { it.isNotEmpty() && !it.startsWith("#") }
            } ?: listOf(DEFAULT_USER_AGENT)
        } catch (e: Exception) {
            // Use default User-Agent if loading fails
            listOf(DEFAULT_USER_AGENT)
        }
    }

    override fun provide(): String {
        if (userAgents.isEmpty()) {
            return DEFAULT_USER_AGENT
        }

        return if (random) {
            userAgents[Random.nextInt(userAgents.size)]
        } else {
            val userAgent = userAgents[currentIndex]
            currentIndex = (currentIndex + 1) % userAgents.size
            userAgent
        }
    }

    companion object {
        private const val DEFAULT_USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    }
}

/**
 * Custom User-Agent list provider
 *
 * @property userAgents User-Agent list
 * @property random Random selection
 */
class CustomUserAgentProvider(
    private val userAgents: List<String>,
    private val random: Boolean = true
) : UserAgentProvider {

    private var currentIndex = 0

    init {
        require(userAgents.isNotEmpty()) { "User-Agent list cannot be empty" }
    }

    override fun provide(): String {
        return if (random) {
            userAgents[Random.nextInt(userAgents.size)]
        } else {
            val userAgent = userAgents[currentIndex]
            currentIndex = (currentIndex + 1) % userAgents.size
            userAgent
        }
    }
}
