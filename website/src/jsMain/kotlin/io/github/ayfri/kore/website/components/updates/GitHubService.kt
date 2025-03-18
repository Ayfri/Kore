package io.github.ayfri.kore.website.components.updates

import io.github.ayfri.kore.website.gitHubReleases

data object GitHubService {
	fun getLatestReleases(perPage: Int = 10) = gitHubReleases.take(perPage)
	fun getReleases() = gitHubReleases
	fun getRelease(tagName: String) = gitHubReleases.find { it.tagName == tagName }
}
