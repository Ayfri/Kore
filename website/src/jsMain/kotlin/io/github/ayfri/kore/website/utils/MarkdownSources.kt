package io.github.ayfri.kore.website.utils

import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlin.js.Json

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
	val obj = JSON.parse<Json>(jsonText)
	val keys = Object.keys(obj)
	return keys.associateWith { key -> obj[key] as String }
}
