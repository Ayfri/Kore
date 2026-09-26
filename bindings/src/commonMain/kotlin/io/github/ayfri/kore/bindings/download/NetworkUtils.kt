package io.github.ayfri.kore.bindings.download

import kotlinx.serialization.json.Json

/** Decodes API responses into the few fields the downloaders read. */
internal val apiJson = Json { ignoreUnknownKeys = true }

/** Percent-encodes [value] for a URL path segment or query value, keeping only the RFC 3986 unreserved characters. */
internal fun encodeUrlComponent(value: String) = buildString {
	value.encodeToByteArray().forEach { byte ->
		val code = byte.toInt() and 0xFF
		val char = code.toChar()
		if (code < 128 && char.isLetterOrDigit() || char in "-._~") append(char)
		else append('%').append(code.toString(16).uppercase().padStart(2, '0'))
	}
}

internal suspend fun fetchJsonString(url: String, headers: Map<String, String> = emptyMap()) = try {
	val response = httpRequest(
		url,
		headers = buildMap {
			put("User-Agent", "Kore")
			put("Accept", "application/json")
			putAll(headers)
		},
	)

	require(response.statusCode != 404) { "Resource not found: $url (404)" }
	require(response.statusCode == 200) { "API error: $url (HTTP ${response.statusCode})" }

	response.bytes.decodeToString()
} catch (e: Exception) {
	if (e is IllegalArgumentException) throw e
	throw IllegalArgumentException("Failed to fetch from API: ${e.message}", e)
}

internal suspend fun urlExists(url: String) = try {
	httpRequest(url, method = "HEAD").statusCode == 200
} catch (e: Exception) {
	false
}
