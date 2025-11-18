package io.github.ayfri.kore.bindings.download

import io.github.ayfri.kore.bindings.getFromCacheOrDownload
import java.net.HttpURLConnection
import java.net.URI
import java.nio.file.Path

/**
 * Downloads datapacks from GitHub repositories.
 * Supports multiple patterns:
 * - Repository download: `user.repo:tag` (tag can be a release tag, commit, or branch)
 * - Release asset: `user.repo:tag:asset-name.zip`
 *
 * Tag parameter is optional. If omitted, uses the default branch (usually main/master).
 *
 * No API key required - uses public GitHub APIs with standard rate limiting.
 */
data object GitHubDownloader {
	private const val GITHUB_API_BASE = "https://api.github.com"
	private const val GITHUB_ARCHIVE = "https://github.com"

	/**
	 * Represents a parsed GitHub reference.
	 */
	data class GitHubRef(
		val owner: String,
		val repo: String,
		val tag: String? = null,
		val assetName: String? = null,
	)

	/**
	 * Parses a GitHub reference string in format: `user.repo:tag:asset-name`
	 * - `tag` can be a release tag, commit hash, or branch name
	 * - `tag` is optional (uses default branch if omitted)
	 * - `asset-name` is only valid for release assets (must be a .zip file)
	 *
	 * Examples:
	 * - `ayfri.my-datapack` → uses default branch
	 * - `ayfri.my-datapack:v1.0.0` → specific release tag
	 * - `ayfri.my-datapack:main` → specific branch
	 * - `ayfri.my-datapack:abc123def` → specific commit
	 * - `ayfri.my-datapack:v1.0.0:pack.zip` → specific release asset
	 *
	 * @throws IllegalArgumentException if format is invalid
	 */
	fun parseReference(reference: String): GitHubRef {
		val parts = reference.split(":")

		require(parts.isNotEmpty() && parts[0].isNotEmpty()) { "GitHub reference cannot be empty" }

		val repoParts = parts[0].split(".")
		require(repoParts.size == 2 && repoParts[0].isNotEmpty() && repoParts[1].isNotEmpty()) {
			"Invalid GitHub reference format. Expected 'user.repo:tag:asset' format. Got: '$reference'"
		}

		val owner = repoParts[0]
		val repo = repoParts[1]
		val tag = parts.getOrNull(1)?.ifEmpty { null }
		val assetName = parts.getOrNull(2)?.ifEmpty { null }

		// Validate asset name if provided
		require(assetName?.endsWith(".zip") != true) { "Asset name must be a .zip file. Got: '$assetName'" }

		// Ensure tag is provided if asset is specified
		require(assetName == null && tag != null) { "Tag must be specified when downloading release assets" }

		return GitHubRef(owner, repo, tag, assetName)
	}

	/**
	 * Downloads a GitHub repository or release asset.
	 * Uses the cache system for storing downloaded files.
	 *
	 * @param reference GitHub reference in format: `user.repo:tag:asset-name`
	 * @param skipCache If true, force redownload
	 * @return Pair of (local path to file, filename)
	 */
	fun download(reference: String, skipCache: Boolean = false): Pair<Path, String> {
		val ref = parseReference(reference)

		return when {
			ref.assetName != null -> downloadReleaseAsset(ref, skipCache)
			ref.tag != null -> downloadArchive(ref, skipCache)
			else -> downloadDefaultBranch(ref, skipCache)
		}
	}

	/**
	 * Downloads a specific release asset (.zip file).
	 * Uses the GitHub Releases API to find the asset download URL.
	 */
	private fun downloadReleaseAsset(ref: GitHubRef, skipCache: Boolean): Pair<Path, String> {
		// Get release data from API
		val releaseUrl = "$GITHUB_API_BASE/repos/${ref.owner}/${ref.repo}/releases/tags/${ref.tag}"
		val releaseJson = fetchJsonString(releaseUrl)

		// Parse out the browser_download_url field for the matching asset
		val assetNameEscaped = ref.assetName!!.replace(".", "\\.")
		val assetPattern = """"name"\s*:\s*"$assetNameEscaped"[^}]*?"browser_download_url"\s*:\s*"([^"]+)"""".toRegex()
		val match = assetPattern.find(releaseJson)
			?: throw IllegalArgumentException(
				"Asset '${ref.assetName}' not found in release '${ref.tag}' of '${ref.owner}/${ref.repo}'"
			)

		val downloadUrl = match.groupValues[1]
		println("Downloading release asset from GitHub: ${ref.owner}/${ref.repo}@${ref.tag}/${ref.assetName}")
		return getFromCacheOrDownload(downloadUrl, skipCache)
	}

	/**
	 * Downloads a repository as a ZIP archive for a specific tag/branch/commit.
	 */
	private fun downloadArchive(ref: GitHubRef, skipCache: Boolean): Pair<Path, String> {
		println("Downloading GitHub archive: ${ref.owner}/${ref.repo}@${ref.tag}")

		// Try as a tag first
		var downloadUrl = "$GITHUB_ARCHIVE/${ref.owner}/${ref.repo}/archive/refs/tags/${ref.tag}.zip"
		if (urlExists(downloadUrl)) {
			return getFromCacheOrDownload(downloadUrl, skipCache)
		}

		// Try as a branch
		downloadUrl = "$GITHUB_ARCHIVE/${ref.owner}/${ref.repo}/archive/refs/heads/${ref.tag}.zip"
		if (urlExists(downloadUrl)) {
			return getFromCacheOrDownload(downloadUrl, skipCache)
		}

		// Try as a commit - GitHub allows downloading any commit as archive
		downloadUrl = "$GITHUB_ARCHIVE/${ref.owner}/${ref.repo}/archive/${ref.tag}.zip"
		if (urlExists(downloadUrl)) {
			return getFromCacheOrDownload(downloadUrl, skipCache)
		}

		throw IllegalArgumentException(
			"Could not find tag, branch, or commit '${ref.tag}' in ${ref.owner}/${ref.repo}"
		)
	}

	/**
	 * Downloads the default branch (main/master) of a repository.
	 */
	private fun downloadDefaultBranch(ref: GitHubRef, skipCache: Boolean): Pair<Path, String> {
		// Get repository info to find default branch
		val repoUrl = "$GITHUB_API_BASE/repos/${ref.owner}/${ref.repo}"
		val repoJson = fetchJsonString(repoUrl)

		// Extract default_branch from JSON
		val branchPattern = """"default_branch"\s*:\s*"([^"]+)"""".toRegex()
		val match = branchPattern.find(repoJson)
			?: throw IllegalArgumentException("Could not determine default branch for ${ref.owner}/${ref.repo}")

		val defaultBranch = match.groupValues[1]
		println("Downloading GitHub archive: ${ref.owner}/${ref.repo} (default branch: $defaultBranch)")

		val downloadUrl = "$GITHUB_ARCHIVE/${ref.owner}/${ref.repo}/archive/refs/heads/$defaultBranch.zip"
		return getFromCacheOrDownload(downloadUrl, skipCache)
	}

	/**
	 * Checks if a URL is accessible (returns 200 OK).
	 */
	private fun urlExists(url: String) = try {
		val connection = URI.create(url).toURL().openConnection() as HttpURLConnection
		connection.requestMethod = "HEAD"
		connection.connectTimeout = 3000
		connection.readTimeout = 3000
		connection.responseCode == 200
	} catch (e: Exception) {
		false
	}

	/**
	 * Fetches JSON data from a GitHub API endpoint as a raw string.
	 * Handles common errors gracefully.
	 */
	private fun fetchJsonString(url: String): String {
		return try {
			val connection = URI.create(url).toURL().openConnection()
			connection.connectTimeout = 5000
			connection.readTimeout = 5000
			connection.connect()

			val responseCode = (connection as HttpURLConnection).responseCode
			require(responseCode != 404) { "GitHub resource not found: $url (404)" }
			require(responseCode == 200) { "GitHub API error: $url (HTTP $responseCode)" }

			connection.getInputStream().use { input ->
				input.bufferedReader().readText()
			}
		} catch (e: Exception) {
			if (e is IllegalArgumentException) throw e
			throw IllegalArgumentException("Failed to fetch from GitHub API: ${e.message}", e)
		}
	}
}
