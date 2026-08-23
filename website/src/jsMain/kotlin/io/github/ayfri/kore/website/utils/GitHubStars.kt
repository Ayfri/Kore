package io.github.ayfri.kore.website.utils

import io.github.ayfri.kore.website.externals.Intl
import io.github.ayfri.kore.website.externals.numberFormatOptions
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

private val compactNumber = Intl.NumberFormat("en", numberFormatOptions(notation = "compact", maximumFractionDigits = 1))

/** Formats a star count like GitHub badges do, e.g. `1234` -> `1.2k`. */
fun formatStarsCount(count: Int) = compactNumber.format(count).lowercase()
