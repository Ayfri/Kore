package io.github.ayfri.kore.bindings.download

import io.github.ayfri.kore.bindings.getFromCacheOrDownload
import java.nio.file.Path

/**
 * Downloads datapacks from Modrinth.
 * Supports patterns:
 * - Project latest: `modrinth:slug`
 * - Specific version: `modrinth:slug:version`
 */
data object ModrinthDownloader : Downloader {
	private const val API_BASE = "https://api.modrinth.com/v2"

	override fun match(source: String) = source.startsWith("modrinth:")

	override fun download(reference: String, skipCache: Boolean) =
		downloadVersion(parseReference(reference.removePrefix("modrinth:")), skipCache)

	/**
	 * Represents a parsed Modrinth reference.
	 */
	data class ModrinthRef(
		val slug: String,
		val version: String? = null,
	)

	/**
	 * Parses a Modrinth reference string in format: `slug:version`
	 * - `version` is optional (uses latest version if omitted)
	 */
	fun parseReference(reference: String): ModrinthRef {
		val parts = reference.split(":")
		return ModrinthRef(parts[0], parts.getOrNull(1))
	}

	private fun downloadVersion(ref: ModrinthRef, skipCache: Boolean): Pair<Path, String> {
		val versionsUrl = "$API_BASE/project/${ref.slug}/version"
		val json = fetchJsonString(versionsUrl)

		// Split JSON array into objects roughly
		val versionObjects = json.trim().removePrefix("[").removeSuffix("]").split("},{", "}, {")

		val targetObject = if (ref.version != null) {
			versionObjects.find { it.contains(""""version_number":"${ref.version}"""") }
				?: throw IllegalArgumentException("Version '${ref.version}' not found for Modrinth project '${ref.slug}'")
		} else {
			versionObjects.firstOrNull()
				?: throw IllegalArgumentException("No versions found for Modrinth project '${ref.slug}'")
		}

		val urlPattern = """"url"\s*:\s*"([^"]+)"""".toRegex()
		val match = urlPattern.find(targetObject)
			?: throw IllegalArgumentException("No file URL found in version object for '${ref.slug}'")

		val downloadUrl = match.groupValues[1]
		println("Downloading from Modrinth: ${ref.slug} (url: $downloadUrl)")
		return getFromCacheOrDownload(downloadUrl, skipCache)
	}
}
