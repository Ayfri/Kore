package io.github.ayfri.kore.website.components.updates

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.Div
import kotlin.js.Date

private class ReleaseStat(val value: String, val label: String)

@Composable
fun ReleaseStats(allReleases: List<GitHubRelease>) {
	Style(ReleaseStatsStyle)

	val stats = remember(allReleases) {
		listOf(
			ReleaseStat("${allReleases.size}", "Releases"),
			ReleaseStat(GitHubService.latestRelease?.koreVersion ?: "N/A", "Latest version"),
			ReleaseStat("${allReleases.mapNotNullTo(mutableSetOf()) { it.mainMinecraftVersion }.size}", "Minecraft versions"),
			ReleaseStat(allReleases.minOfOrNull { it.publishedTime }?.let { "${Date(it).getFullYear()}" } ?: "N/A", "Releasing since"),
		)
	}

	Div({ classes(ReleaseStatsStyle.stats) }) {
		stats.forEach { stat ->
			Div({ classes(ReleaseStatsStyle.stat) }) {
				Span(stat.value, ReleaseStatsStyle.statValue)
				Span(stat.label, ReleaseStatsStyle.statLabel)
			}
		}
	}
}

object ReleaseStatsStyle : StyleSheet() {
	val stats by style {
		backgroundColor(Color("var(--landing-border)"))
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(1.cssRem)
		display(DisplayStyle.Grid)
		gap(1.px)
		gridTemplateColumns("repeat(4, minmax(0, 1fr))")
		marginTop(0.8.cssRem)
		overflow(Overflow.Hidden)

		mdMax(self) {
			gridTemplateColumns("repeat(2, minmax(0, 1fr))")
		}
	}

	val stat by style {
		backgroundColor(Color("var(--landing-card)"))
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.15.cssRem)
		justifyContent(JustifyContent.Center)
		padding(0.65.cssRem, 1.cssRem)
	}

	val statValue by style {
		color(Color("var(--landing-accent-strong)"))
		fontFamily("JetBrains Mono", "monospace")
		fontSize(1.2.cssRem)
		fontWeight(700)
		lineHeight(1.2.number)
	}

	val statLabel by style {
		color(Color("var(--landing-muted)"))
		fontSize(0.78.cssRem)
		whiteSpace(WhiteSpace.NoWrap)
	}
}
