package io.github.ayfri.kore.bindings

import java.io.File
import java.net.URI
import java.net.URLDecoder
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.Path

private val cacheDir: File by lazy {
	val dir = Path(System.getProperty("user.home"), ".kore", "cache", "datapacks").toFile()
	if (!dir.exists()) dir.mkdirs()
	dir
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
	val cachedFile = File(cacheDir, "$cacheKey-$decodedFileName")

	if (cachedFile.exists() && !skipCache) {
        val sizeInBytes = cachedFile.length()
        val sizeInMB = sizeInBytes / (1024.0 * 1024.0)
        println("Using cached datapack: ${cachedFile.absolutePath} (%.2f MB)".format(sizeInMB))
		return cachedFile.toPath() to decodedFileName
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
			Files.copy(input, cachedFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
		}
		val sizeInBytes = cachedFile.length()
		val sizeInMB = sizeInBytes / (1024.0 * 1024.0)
		println("Downloaded and cached: ${cachedFile.absolutePath} (%.2f MB)".format(sizeInMB))
		return cachedFile.toPath() to decodedFileName
	} catch (e: Exception) {
		throw IllegalArgumentException("Failed to download datapack from $url: ${e.message}", e)
	}
}
