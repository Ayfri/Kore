package io.github.ayfri.kore.bindings.download

import java.net.HttpURLConnection
import java.net.URI

internal fun fetchJsonString(url: String, headers: Map<String, String> = emptyMap()) = try {
	val connection = URI.create(url).toURL().openConnection() as HttpURLConnection
	connection.connectTimeout = 5000
	connection.readTimeout = 5000
	connection.setRequestProperty("User-Agent", "Kore")
	connection.setRequestProperty("Accept", "application/json")
	headers.forEach { (key, value) -> connection.setRequestProperty(key, value) }
	connection.connect()

	val responseCode = connection.responseCode
	require(responseCode != 404) { "Resource not found: $url (404)" }
	require(responseCode == 200) { "API error: $url (HTTP $responseCode)" }

	connection.getInputStream().use { input ->
		input.bufferedReader().readText()
	}
} catch (e: Exception) {
	if (e is IllegalArgumentException) throw e
	throw IllegalArgumentException("Failed to fetch from API: ${e.message}", e)
}

internal fun urlExists(url: String) = try {
	val connection = URI.create(url).toURL().openConnection() as HttpURLConnection
	connection.requestMethod = "HEAD"
	connection.connectTimeout = 3000
	connection.readTimeout = 3000
	connection.responseCode == 200
} catch (e: Exception) {
	false
}
