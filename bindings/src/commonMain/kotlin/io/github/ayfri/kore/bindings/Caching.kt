package io.github.ayfri.kore.bindings

import io.github.ayfri.kore.bindings.download.cacheDir
import io.github.ayfri.kore.bindings.download.cacheFileExists
import io.github.ayfri.kore.bindings.download.cacheReadFile
import io.github.ayfri.kore.bindings.download.cacheWriteFile
import io.github.ayfri.kore.bindings.download.httpRequest
import kotlinx.io.files.Path

/**
 * Downloads a file from URL and caches it locally (filesystem on the JVM/Node.js, OPFS in the browser).
 * Returns the path to the cached file and the actual filename (decoded from URL).
 *
 * @param url The URL to download from
 * @param skipCache If true, always redownload the file even if cached
 */
internal suspend fun getFromCacheOrDownload(url: String, skipCache: Boolean = false) =
	getFromCacheOrDownload(url, null, emptyMap(), skipCache)

internal suspend fun getFromCacheOrDownload(
	url: String,
	requestBody: String?,
	requestHeaders: Map<String, String>,
	skipCache: Boolean = false,
): Pair<Path, String> {
	// Extract filename from URL and decode it
	val encodedFileName = url.substringAfterLast('/').ifEmpty { "datapack.zip" }
	val decodedFileName = percentDecode(encodedFileName)

	// Include request options in cache key so different authenticated/custom payload downloads don't collide.
	val cacheFingerprint = buildString {
		append(url)
		append('|')
		append(requestBody ?: "")
		append('|')
		append(
			requestHeaders
				.entries
				.sortedBy { it.key }
				.joinToString("&") { (key, value) -> "$key=$value" }
		)
	}
	val cacheKey = cacheFingerprint.hashCode().toString(16)
	val cachedFile = Path(cacheDir(), "$cacheKey-$decodedFileName")

	if (!skipCache && cacheFileExists(cachedFile)) {
		println("Using cached datapack: $cachedFile")
		return cachedFile to decodedFileName
	}

	if (skipCache && cacheFileExists(cachedFile)) {
		println("Skipping cache, redownloading datapack from: $url")
	} else {
		println("Downloading datapack from: $url")
	}

	try {
		val method = if (requestBody != null) "POST" else "GET"
		val headers = if (requestBody != null && requestHeaders.keys.none { it.equals("Content-Type", ignoreCase = true) }) {
			requestHeaders + ("Content-Type" to "application/json; charset=utf-8")
		} else {
			requestHeaders
		}
		val response = httpRequest(url, method, headers, requestBody?.encodeToByteArray())
		require(response.statusCode in 200..299) { "HTTP ${response.statusCode}" }

		cacheWriteFile(cachedFile, response.bytes)
		val sizeInMb = (response.bytes.size / (1024.0 * 1024.0) * 100).let { (it + 0.5).toLong() / 100.0 }
		println("Downloaded and cached: $cachedFile ($sizeInMb MB)")
		return cachedFile to decodedFileName
	} catch (e: Exception) {
		throw IllegalArgumentException("Failed to download datapack from $url: ${e.message}", e)
	}
}

/** Minimal multiplatform equivalent of `java.net.URLDecoder.decode(s, "UTF-8")` for a URL path segment. */
private fun percentDecode(value: String): String {
	if ('%' !in value && '+' !in value) return value

	val bytes = mutableListOf<Byte>()
	var i = 0
	while (i < value.length) {
		when (val c = value[i]) {
			'%' -> {
				bytes += value.substring(i + 1, i + 3).toInt(16).toByte()
				i += 3
			}

			'+' -> {
				bytes += ' '.code.toByte()
				i++
			}

			else -> {
				bytes += c.toString().encodeToByteArray().toList()
				i++
			}
		}
	}
	return bytes.toByteArray().decodeToString()
}
