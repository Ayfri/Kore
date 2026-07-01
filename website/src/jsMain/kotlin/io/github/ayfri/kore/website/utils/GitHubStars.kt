package io.github.ayfri.kore.website.utils

import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlin.js.Date
import kotlin.js.Json

private const val GITHUB_STARS_CACHE_KEY = "kore-github-stars"
private const val GITHUB_STARS_CACHE_DURATION_MS = 60 * 60 * 1000

private data class CachedStars(val count: Int, val timestamp: Double)

/**
 * Fetches the star count of a GitHub repository from the official, unauthenticated GitHub REST API.
 * Result is cached in `localStorage` for an hour to stay well within GitHub's rate limits.
 */
suspend fun fetchGitHubStars(owner: String, repo: String): Int? {
	val cached = readCachedStars()
	if (cached != null && Date.now() - cached.timestamp < GITHUB_STARS_CACHE_DURATION_MS) return cached.count

	return try {
		val response = window.fetch("https://api.github.com/repos/$owner/$repo").await()
		if (!response.ok) return cached?.count

		val json = JSON.parse<Json>(response.text().await())
		val count = (json["stargazers_count"] as? Number)?.toInt() ?: return cached?.count

		localStorage.setItem(GITHUB_STARS_CACHE_KEY, "$count:${Date.now()}")
		count
	} catch (e: Throwable) {
		cached?.count
	}
}

private fun readCachedStars(): CachedStars? {
	val raw = localStorage.getItem(GITHUB_STARS_CACHE_KEY) ?: return null
	val (rawCount, rawTimestamp) = raw.split(":").takeIf { it.size == 2 } ?: return null
	val count = rawCount.toIntOrNull() ?: return null
	val timestamp = rawTimestamp.toDoubleOrNull() ?: return null
	return CachedStars(count, timestamp)
}

/** Formats a star count like GitHub badges do, e.g. `1234` -> `1.2k`. */
fun formatStarsCount(count: Int): String {
	fun compact(value: Int, divisor: Int, suffix: String): String {
		val tenths = value * 10 / divisor
		val formatted = if (tenths % 10 == 0) "${tenths / 10}" else "${tenths / 10}.${tenths % 10}"
		return "$formatted$suffix"
	}

	return when {
		count >= 1_000_000 -> compact(count, 1_000_000, "m")
		count >= 1_000 -> compact(count, 1_000, "k")
		else -> count.toString()
	}
}
