package io.github.ayfri.kore.website.components.updates

import io.github.ayfri.kore.website.utils.extractMainMinecraftVersion
import io.github.ayfri.kore.website.utils.extractMinecraftVersion
import io.github.ayfri.kore.website.utils.isSnapshotVersion

data class GitHubRelease(
	val id: Int,
	val name: String,
	val tagName: String,
	val htmlUrl: String,
	val url: String,
	val createdAt: String,
	val publishedAt: String,
	val body: String,
	val isPrerelease: Boolean,
	val assets: List<GitHubAsset> = emptyList()
) {
	/**
	 * Checks if a release matches the specified filtering criteria.
	 * @param options The filtering options
	 * @return true if the release matches the criteria, false otherwise
	 */
	fun matchesFilters(options: ReleaseFilterOptions): Boolean {
		// Check the pre-release filter
		if (!options.showPreReleases && isPrerelease) {
			return false
		}
		
		// Check the snapshot filter
		val mcVersion = extractMinecraftVersion(tagName)
		if (!options.showSnapshots && isSnapshotVersion(mcVersion)) {
			return false
		}

		// Check the selected Minecraft versions
		if (options.selectedMinecraftVersions.isNotEmpty()) {
			val mainVersion = extractMainMinecraftVersion(mcVersion)

			if (mainVersion == null || !options.selectedMinecraftVersions.contains(mainVersion)) {
				return false
			}
		}

		// Check the text search
		if (options.searchQuery.isNotEmpty()) {
			val query = options.searchQuery.lowercase()
			return name.lowercase().contains(query) == true ||
				tagName.lowercase().contains(query) ||
				body.lowercase().contains(query)
		}

		return true
	}
}

data class GitHubAsset(
	val id: Int,
	val name: String,
	val browserDownloadUrl: String,
	val contentType: String,
	val size: Float,
	val downloadCount: Int
)
