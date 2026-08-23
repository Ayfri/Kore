package io.github.ayfri.kore.website.components.updates

import io.github.ayfri.kore.website.gitHubReleases

data object GitHubService {
	/** The most recently published release, resolved once since the release list is baked in at build time. */
	val latestRelease by lazy { gitHubReleases.maxByOrNull { it.publishedTime } }

	fun getLatestReleases(perPage: Int = 10) = gitHubReleases.take(perPage)
	fun getReleases() = gitHubReleases
	fun getRelease(tagName: String) = gitHubReleases.find { it.tagName == tagName }
}
