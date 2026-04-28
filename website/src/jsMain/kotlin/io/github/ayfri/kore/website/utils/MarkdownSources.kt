package io.github.ayfri.kore.website.utils

import kotlinx.browser.window
import kotlinx.coroutines.await

private var cachedMarkdownSources: Map<String, String>? = null

suspend fun loadMarkdownSources(): Map<String, String> {
	cachedMarkdownSources?.let { return it }
	val response = window.fetch("/markdown-sources.json").await()
	check(response.ok) { "Failed to load markdown-sources.json: HTTP ${response.status}" }
	val text = response.text().await()
	val map = parseMarkdownSourcesJson(text)
	cachedMarkdownSources = map
	return map
}

private fun parseMarkdownSourcesJson(jsonText: String): Map<String, String> {
	val obj = JSON.parse<Map<String, String>>(jsonText)
	val keys = obj.keys
	return keys.associateWith { key -> obj[key] ?: error("Missing value for key '$key' in markdown-sources.json") }
}
