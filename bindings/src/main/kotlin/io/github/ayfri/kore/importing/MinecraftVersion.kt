package io.github.ayfri.kore.bindings

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Get Minecraft version from gradle.properties
 */
fun getMinecraftVersion(): String {
	val rootDir = Paths.get(".").toAbsolutePath().normalize().let {
		var current = it
		while (current != null && !Files.exists(current.resolve("gradle.properties"))) {
			current = current.parent
		}
		current ?: it
	}
	val properties = java.util.Properties()
	val propsFile = rootDir.resolve("gradle.properties").toFile()
	if (!propsFile.exists()) {
		throw Exception("gradle.properties not found in project root: $rootDir")
	}

	properties.load(propsFile.inputStream())
	return properties.getProperty("minecraft.version") ?: throw Exception("minecraft.version not found in gradle.properties")
}

/**
 * Get the cache directory for downloaded Minecraft data
 */
fun getCacheDir(): File {
	val rootDir = Paths.get(".").toAbsolutePath().normalize().let {
		var current = it
		while (current != null && !Files.exists(current.resolve("gradle.properties"))) {
			current = current.parent
		}
		current ?: it
	}
	return rootDir.resolve("importing/build/cache").toFile().apply { mkdirs() }
}

/**
 * Get or download a file from cache
 * @param fileName The name to use for the cache file
 * @param url The URL to download from if not in cache
 * @return The file content as a string
 */
fun getFromCacheOrDownload(fileName: String, url: String): String {
	val cacheDir = getCacheDir()
	val cacheFile = File(cacheDir, fileName)

	return if (cacheFile.exists()) {
		cacheFile.readText()
	} else {
		try {
			val content = java.net.URI.create(url).toURL().readText()
			cacheFile.writeText(content)
			content
		} catch (e: Exception) {
			throw Exception("Failed to download $url: ${e.message}", e)
		}
	}
}

internal const val MAIN_GITHUB_URL = "https://raw.githubusercontent.com/PixiGeko/Minecraft-generated-data"
internal val MINECRAFT_VERSION = getMinecraftVersion()

internal fun urlGeneratedData(path: String) = "$MAIN_GITHUB_URL/$MINECRAFT_VERSION/$path"
