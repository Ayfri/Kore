package io.github.ayfri.kore.bindings.download

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
