package io.github.ayfri.kore.website.components.updates

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.ayfri.kore.website.components.common.Stat
import io.github.ayfri.kore.website.components.common.StatGrid
import io.github.ayfri.kore.website.components.common.StatGridStyle
import org.jetbrains.compose.web.css.*
import kotlin.js.Date

@Composable
fun ReleaseStats(allReleases: List<GitHubRelease>) {
	Style(ReleaseStatsStyle)

	val stats = remember(allReleases) {
		listOf(
			Stat("${allReleases.size}", "Releases"),
			Stat(GitHubService.latestRelease?.koreVersion ?: "N/A", "Latest version"),
			Stat("${allReleases.mapNotNullTo(mutableSetOf()) { it.mainMinecraftVersion }.size}", "Minecraft versions"),
			Stat(allReleases.minOfOrNull { it.publishedTime }?.let { "${Date(it).getFullYear()}" } ?: "N/A", "Releasing since"),
		)
	}

	StatGrid(stats, StatGridStyle.compact, ReleaseStatsStyle.stats)
}

object ReleaseStatsStyle : StyleSheet() {
	val stats by style {
		marginTop(0.8.cssRem)
	}
}
