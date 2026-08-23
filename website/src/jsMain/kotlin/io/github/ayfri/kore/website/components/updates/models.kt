package io.github.ayfri.kore.website.components.updates

import io.github.ayfri.kore.website.utils.*
import kotlin.js.Date

/** Stable Minecraft releases from oldest to newest, resolved once and shared by every release. */
private val stableReleasesByDate by lazy {
	GitHubService.getReleases().filter { it.isStableRelease }.sortedBy { it.publishedTime }
}

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
	val assets: List<GitHubAsset> = emptyList(),
) {
	val publishedDate by lazy { Date(publishedAt) }
	val publishedTime by lazy { publishedDate.getTime() }

	val koreVersion by lazy { extractKoreVersion(tagName) }
	val minecraftVersion by lazy { extractMinecraftVersion(tagName).takeIf { "-" in tagName } }

	private val versionKind by lazy { minecraftVersion?.let(MinecraftVersionPattern::of) }
	val isSnapshot get() = versionKind == MinecraftVersionPattern.SNAPSHOT
	val isPreReleaseVersion get() = versionKind == MinecraftVersionPattern.PRE_RELEASE
	val isReleaseCandidate get() = versionKind == MinecraftVersionPattern.RELEASE_CANDIDATE
	val isStableRelease get() = versionKind == MinecraftVersionPattern.RELEASE

	/**
	 * The `major.minor.patch` bucket this release belongs to, used by the version filters.
	 *
	 * A tag names the version it targets (`26.2-pre-4` and `26.2-snapshot-1` both target 26.2), so it is read straight
	 * from the tag. Only the old weekly snapshots (`23w41a`) carry no version and are attached to the release they led to.
	 */
	val baseMinecraftVersion by lazy {
		extractBaseMinecraftVersion(minecraftVersion) ?: extractBaseMinecraftVersion(nextMinecraftReleaseVersion)
	}

	/** [baseMinecraftVersion] reduced to `major.minor`. */
	val mainMinecraftVersion by lazy { extractMainMinecraftVersion(baseMinecraftVersion) }

	/** Lowercased haystack for the search box, built once instead of on every keystroke. */
	private val searchIndex by lazy { "$name $tagName $body".lowercase() }

	/** The first stable release published after this one, or null when none followed it yet. */
	private val nextMinecraftReleaseVersion by lazy {
		stableReleasesByDate.firstOrNull { it.publishedTime > publishedTime }?.minecraftVersion
	}

	/**
	 * Checks if a release matches the specified filtering criteria.
	 * @param options The filtering options
	 * @return true if the release matches the criteria, false otherwise
	 */
	fun matchesFilters(options: ReleaseFilterOptions): Boolean {
		if (!options.showPreReleases && isPreReleaseVersion) return false
		if (!options.showReleaseCandidates && isReleaseCandidate) return false
		if (!options.showSnapshots && isSnapshot) return false

		if (options.selectedMinecraftVersions.isNotEmpty()) {
			val base = baseMinecraftVersion ?: return false
			val matches = options.selectedMinecraftVersions.any { selected ->
				// A `major.minor` selection also covers every patch below it.
				base == selected || (selected.count { it == '.' } == 1 && base.startsWith("$selected."))
			}
			if (!matches) return false
		}

		return options.searchQuery.isEmpty() || options.searchQuery.lowercase() in searchIndex
	}
}

data class GitHubAsset(
	val id: Int,
	val name: String,
	val browserDownloadUrl: String,
	val contentType: String,
	val size: Float,
	val downloadCount: Int,
)
