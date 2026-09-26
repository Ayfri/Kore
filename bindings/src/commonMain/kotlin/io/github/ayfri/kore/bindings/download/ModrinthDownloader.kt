package io.github.ayfri.kore.bindings.download

import io.github.ayfri.kore.bindings.getFromCacheOrDownload
import io.github.ayfri.kore.utils.KoreLogger
import kotlinx.io.files.Path
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Downloads datapacks from Modrinth.
 * Supports patterns:
 * - Project latest: `modrinth:slug`, the newest version published for the `datapack` loader
 * - Specific version: `modrinth:slug:version`, matched on its version number or id
 */
internal data object ModrinthDownloader : Downloader {
	private const val API_BASE = "https://api.modrinth.com/v2"
	private const val DATAPACK_LOADER = "datapack"

	@Serializable
	private data class Version(
		val id: String,
		@SerialName("version_number") val versionNumber: String,
		val loaders: List<String> = emptyList(),
		val files: List<File> = emptyList(),
	)

	@Serializable
	private data class File(val url: String, val primary: Boolean = false)

	override fun match(source: String) = source.startsWith("modrinth:")

	override suspend fun download(reference: String, skipCache: Boolean) =
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

	/**
	 * Returns the primary file URL (or the first file's) of [version] in a `/project/{slug}/version` response, or of
	 * the newest `datapack` loader version when [version] is `null`. Modrinth lists versions newest first.
	 */
	internal fun downloadUrl(versionsJson: String, slug: String, version: String?): String {
		val versions = apiJson.decodeFromString<List<Version>>(versionsJson)
		val target = when (version) {
			null -> versions.firstOrNull { DATAPACK_LOADER in it.loaders }
				?: throw IllegalArgumentException("No '$DATAPACK_LOADER' version found for Modrinth project '$slug'")

			else -> versions.firstOrNull { it.versionNumber == version || it.id == version }
				?: throw IllegalArgumentException("Version '$version' not found for Modrinth project '$slug'")
		}

		val file = target.files.firstOrNull { it.primary } ?: target.files.firstOrNull()
			?: throw IllegalArgumentException("Version '${target.versionNumber}' of Modrinth project '$slug' has no file")
		return file.url
	}

	private suspend fun downloadVersion(ref: ModrinthRef, skipCache: Boolean): Pair<Path, String> {
		val versionsJson = fetchJsonString("$API_BASE/project/${encodeUrlComponent(ref.slug)}/version")
		val downloadUrl = downloadUrl(versionsJson, ref.slug, ref.version)
		KoreLogger.info("Downloading from Modrinth: ${ref.slug} (url: $downloadUrl)")
		return getFromCacheOrDownload(downloadUrl, skipCache)
	}
}
