package io.github.ayfri.kore.website.components.updates

import io.github.ayfri.kore.website.utils.MinecraftVersionPattern
import io.github.ayfri.kore.website.utils.extractKoreVersion
import io.github.ayfri.kore.website.utils.extractMainMinecraftVersion
import io.github.ayfri.kore.website.utils.extractMinecraftVersion
import kotlin.js.Date

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
	val publishedDate get() = Date(publishedAt)

	fun getKoreVersion() = extractKoreVersion(tagName)
	fun getMinecraftVersion() = extractMinecraftVersion(tagName).takeIf { "-" in tagName }
	fun getMainMinecraftVersion() = extractMainMinecraftVersion(getMinecraftVersion())

	fun isPreRelease() = getMinecraftVersion()?.let { MinecraftVersionPattern.PRE_RELEASE.matches(it) } == true
	fun isRelease() = getMinecraftVersion()?.let { MinecraftVersionPattern.RELEASE.matches(it) } == true
	fun isReleaseCandidate() = getMinecraftVersion()?.let { MinecraftVersionPattern.RELEASE_CANDIDATE.matches(it) } == true
	fun isSnapshot() = getMinecraftVersion()?.let { MinecraftVersionPattern.SNAPSHOT.matches(it) } == true

	fun findNextMinecraftReleaseVersion(): String {
		val allReleases = GitHubService.getReleases()
		val releasesSortedByPublishDate = allReleases.sortedBy { it.publishedDate.getTime() }
		val nextMainRelease = releasesSortedByPublishDate.reversed()
			.firstOrNull { (it.publishedDate.getTime() > publishedDate.getTime()) && it.isRelease() }
		if (nextMainRelease?.getMinecraftVersion() != null) return nextMainRelease.getMinecraftVersion()!!

		// Gets the last main version and increments it, ex: 1.21.4 -> 1.21.5
		val latestPreviousVersion = releasesSortedByPublishDate.first { it.isRelease() }
		val mainVersion = latestPreviousVersion.getMinecraftVersion()!!.substringBeforeLast(".")
		val minorPatch = latestPreviousVersion.getMinecraftVersion()!!.substringAfterLast(".").toIntOrNull() ?: -1
		return "$mainVersion.${minorPatch + 1}"
	}

	/**
	 * Checks if a release matches the specified filtering criteria.
	 * @param options The filtering options
	 * @return true if the release matches the criteria, false otherwise
	 */
	fun matchesFilters(options: ReleaseFilterOptions): Boolean {
		// Check the pre-release filter
		if (!options.showPreReleases && isPreRelease()) return false

		// Check the snapshot filter
		if (!options.showSnapshots && isSnapshot()) return false

		if (options.selectedMinecraftVersions.isNotEmpty()) {
			val mainVersion = getMainMinecraftVersion()
			if (mainVersion == null || !options.selectedMinecraftVersions.contains(mainVersion)) {
				return false
			}
		}

		// Check the text search
		if (options.searchQuery.isNotEmpty()) {
			val query = options.searchQuery.lowercase()
			return name.lowercase().contains(query) || tagName.lowercase().contains(query) || body.lowercase().contains(query)
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
