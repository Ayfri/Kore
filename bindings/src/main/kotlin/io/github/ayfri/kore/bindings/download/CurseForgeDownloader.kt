package io.github.ayfri.kore.bindings.download

import io.github.ayfri.kore.bindings.getFromCacheOrDownload
import java.nio.file.Path

/**
 * Downloads datapacks from CurseForge.
 * Supports patterns:
 * - Project latest: `curseforge:projectId` or `curseforge:slug`
 * - Specific file: `curseforge:projectId:fileId` or `curseforge:slug:fileId`
 * - URL: `curseforge:https://www.curseforge.com/...`
 *
 * Requires `CURSEFORGE_API_KEY` environment variable or system property.
 */
data object CurseForgeDownloader : Downloader {
	private const val API_BASE = "https://api.curseforge.com/v1"
	private val apiKey by lazy {
		System.getenv("CURSEFORGE_API_KEY") ?: System.getProperty("CURSEFORGE_API_KEY")
		?: throw IllegalStateException("CURSEFORGE_API_KEY environment variable or system property is required for CurseForge downloads")
	}

	override fun match(source: String) = source.startsWith("curseforge:")

	override fun download(reference: String, skipCache: Boolean) =
		downloadFile(parseReference(reference.removePrefix("curseforge:")), skipCache)

	/**
	 * Represents a parsed CurseForge reference.
	 */
	data class CurseForgeRef(
		val projectIdentifier: String,
		val fileId: String? = null,
	)

	/**
	 * Parses a CurseForge reference string.
	 * Supports:
	 * - Project ID: `123456`
	 * - Project ID + File ID: `123456:789`
	 * - Slug: `my-mod-slug`
	 * - Slug + File ID: `my-mod-slug:789`
	 * - URL: `https://www.curseforge.com/minecraft/mc-mods/my-mod-slug`
	 */
	fun parseReference(reference: String): CurseForgeRef {
		// Handle URL
		if (reference.startsWith("http")) {
			val slug = reference.trim().trimEnd('/').substringAfterLast('/')
			return CurseForgeRef(slug)
		}

		val parts = reference.split(":")
		return CurseForgeRef(parts[0], parts.getOrNull(1))
	}

	private fun downloadFile(ref: CurseForgeRef, skipCache: Boolean): Pair<Path, String> {
		val projectId = resolveProjectId(ref.projectIdentifier)

		val url = if (ref.fileId != null) {
			"$API_BASE/mods/$projectId/files/${ref.fileId}"
		} else {
			"$API_BASE/mods/$projectId/files"
		}

		val json = fetchJsonWithKey(url)
		// If we requested a list (no fileId), we want the first one (latest).
		// If we requested a specific file, the JSON is that file object.
		val downloadUrl = extractUrl(json)

		println("Downloading from CurseForge: $projectId (url: $downloadUrl)")
		return getFromCacheOrDownload(downloadUrl, skipCache)
	}

	private fun resolveProjectId(identifier: String): String {
		// If it's already a number, return it
		if (identifier.all { it.isDigit() }) return identifier

		// Otherwise, search for the slug
		println("Resolving CurseForge slug: $identifier")
		val searchUrl = "$API_BASE/mods/search?gameId=432&slug=$identifier"
		val json = fetchJsonWithKey(searchUrl)

		val idPattern = """"id"\s*:\s*(\d+)""".toRegex()
		val match = idPattern.find(json)
			?: throw IllegalArgumentException("Could not resolve CurseForge slug '$identifier' to a project ID")

		return match.groupValues[1]
	}

	private fun fetchJsonWithKey(url: String) = fetchJsonString(url, mapOf("x-api-key" to apiKey))

	private fun extractUrl(json: String): String {
		val urlPattern = """"downloadUrl"\s*:\s*"([^"]+)"""".toRegex()
		val match = urlPattern.find(json)
			?: throw IllegalArgumentException("No downloadUrl found in CurseForge response")
		return match.groupValues[1]
	}
}
