package io.github.ayfri.kore.bindings.download

import io.github.ayfri.kore.bindings.getFromCacheOrDownload
import io.github.ayfri.kore.utils.KoreLogger
import kotlinx.io.files.Path
import kotlinx.serialization.Serializable

/**
 * Downloads datapacks from CurseForge.
 * Supports patterns:
 * - Project latest: `curseforge:projectId` or `curseforge:slug`
 * - Specific file: `curseforge:projectId:fileId` or `curseforge:slug:fileId`
 * - URL: `curseforge:https://www.curseforge.com/...`
 *
 * Requires `CURSEFORGE_API_KEY` environment variable
 * or `curseforge.api.key` system property
 */
internal data object CurseForgeDownloader : Downloader {
	private const val API_BASE = "https://api.curseforge.com/v1"
	private const val MINECRAFT_GAME_ID = 432

	private val apiKey by lazy {
		platformEnvVar("CURSEFORGE_API_KEY") ?: platformSystemProperty("curseforge.api.key")
		?: throw IllegalStateException("CURSEFORGE_API_KEY environment variable or curseforge.api.key system property is required for CurseForge downloads")
	}

	@Serializable
	private data class Response<T>(val data: T)

	@Serializable
	private data class Mod(val id: Long, val slug: String)

	@Serializable
	private data class File(val id: Long, val fileName: String = "", val downloadUrl: String? = null)

	override fun match(source: String) = source.startsWith("curseforge:")

	override suspend fun download(reference: String, skipCache: Boolean) =
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
		if (reference.startsWith("http")) {
			val slug = reference.trim().trimEnd('/').substringAfterLast('/')
			return CurseForgeRef(slug)
		}

		val parts = reference.split(":")
		return CurseForgeRef(parts[0], parts.getOrNull(1))
	}

	/** Returns the id of the project whose slug is exactly [slug] in a `/mods/search` response. */
	internal fun projectId(searchJson: String, slug: String) =
		apiJson.decodeFromString<Response<List<Mod>>>(searchJson).data.firstOrNull { it.slug == slug }?.id?.toString()
			?: throw IllegalArgumentException("Could not resolve CurseForge slug '$slug' to a project ID")

	/**
	 * Returns the download URL of the file in a `/mods/{id}/files/{fileId}` response, or of the newest downloadable
	 * file in a `/mods/{id}/files` response. A file with a `null` URL is one whose author disabled third-party downloads.
	 */
	internal fun downloadUrl(filesJson: String, singleFile: Boolean): String {
		val files = when {
			singleFile -> listOf(apiJson.decodeFromString<Response<File>>(filesJson).data)
			else -> apiJson.decodeFromString<Response<List<File>>>(filesJson).data
		}

		return files.filter { it.downloadUrl != null }.maxByOrNull { it.id }?.downloadUrl
			?: throw IllegalArgumentException(
				"No downloadable CurseForge file among ${files.map { it.fileName }}, its author may have disabled third-party downloads"
			)
	}

	private suspend fun downloadFile(ref: CurseForgeRef, skipCache: Boolean): Pair<Path, String> {
		val projectId = resolveProjectId(ref.projectIdentifier)
		val filesUrl = "$API_BASE/mods/$projectId/files${ref.fileId?.let { "/${encodeUrlComponent(it)}" }.orEmpty()}"
		val downloadUrl = downloadUrl(fetchJsonWithKey(filesUrl), singleFile = ref.fileId != null)

		KoreLogger.info("Downloading from CurseForge: $projectId (url: $downloadUrl)")
		return getFromCacheOrDownload(downloadUrl, skipCache)
	}

	private suspend fun resolveProjectId(identifier: String): String {
		if (identifier.all { it.isDigit() }) return identifier

		KoreLogger.info("Resolving CurseForge slug: $identifier")
		return projectId(fetchJsonWithKey("$API_BASE/mods/search?gameId=$MINECRAFT_GAME_ID&slug=${encodeUrlComponent(identifier)}"), identifier)
	}

	private suspend fun fetchJsonWithKey(url: String) = fetchJsonString(url, mapOf("x-api-key" to apiKey))
}
