package io.github.ayfri.kore.bindings

import java.net.URI
import java.net.URLDecoder
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.fileSize
import kotlin.io.path.Path

private val cacheDir: Path by lazy {
	val os = System.getProperty("os.name").lowercase()
	val platformCacheBase = when {
		os.contains("win") -> System.getenv("LOCALAPPDATA") ?: (System.getProperty("user.home") + "/AppData/Local")
		os.contains("mac") -> System.getProperty("user.home") + "/Library/Caches"
		else -> System.getenv("XDG_CACHE_HOME") ?: (System.getProperty("user.home") + "/.cache")
	}
	Path(
		System.getenv("KORE_CACHE_HOME")
		?: System.getProperty("kore.cache.home")
		?: "$platformCacheBase/kore",
		"datapacks"
	).also { it.createDirectories() }
}

/**
 * Downloads a file from URL and caches it locally.
 * Returns the path to the cached file and the actual filename (decoded from URL).
 *
 * @param url The URL to download from
 * @param skipCache If true, always redownload the file even if cached
 */
fun getFromCacheOrDownload(url: String, skipCache: Boolean = false): Pair<Path, String> {
	// Extract filename from URL and decode it
	val encodedFileName = url.substringAfterLast('/').ifEmpty { "datapack.zip" }
	val decodedFileName = URLDecoder.decode(encodedFileName, "UTF-8")

	// Use a hash of the URL as cache key to handle different URLs with same filename
	val cacheKey = url.hashCode().toString(16)
	val cachedFile = cacheDir.resolve("$cacheKey-$decodedFileName")

	if (cachedFile.exists() && !skipCache) {
        val sizeInBytes = cachedFile.fileSize()
        val sizeInMB = sizeInBytes / (1024.0 * 1024.0)
        println("Using cached datapack: ${cachedFile.absolutePathString()} (%.2f MB)".format(sizeInMB))
		return cachedFile to decodedFileName
	}

	if (skipCache && cachedFile.exists()) {
		println("Skipping cache, redownloading datapack from: $url")
	} else {
		println("Downloading datapack from: $url")
	}

	try {
		val connection = URI.create(url).toURL().openConnection()
		connection.connect()
		connection.getInputStream().use { input ->
			Files.copy(input, cachedFile, StandardCopyOption.REPLACE_EXISTING)
		}
		val sizeInBytes = cachedFile.fileSize()
		val sizeInMB = sizeInBytes / (1024.0 * 1024.0)
		println("Downloaded and cached: ${cachedFile.absolutePathString()} (%.2f MB)".format(sizeInMB))
		return cachedFile to decodedFileName
	} catch (e: Exception) {
		throw IllegalArgumentException("Failed to download datapack from $url: ${e.message}", e)
	}
}
