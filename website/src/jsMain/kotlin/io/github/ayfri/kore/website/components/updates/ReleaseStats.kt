package io.github.ayfri.kore.website.components.updates

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.components.icons.mdi.MdiArchive
import com.varabyte.kobweb.silk.components.icons.mdi.MdiCalendarMonth
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Text

@Composable
fun ReleaseStats(allReleases: List<GitHubRelease>, filteredReleases: List<GitHubRelease>) {
	Style(ReleaseStatsStyle)

	// Calculate basic statistics
	val totalReleases = allReleases.size
	val firstReleaseDate = allReleases.minByOrNull { it.publishedAt }?.publishedAt?.let { formatDate(it) } ?: "N/A"
	val latestReleaseDate = allReleases.maxByOrNull { it.publishedAt }?.publishedAt?.let { formatDate(it) } ?: "N/A"

	// Extract Minecraft version statistics using effective versions
	val minecraftVersionStats = allReleases.groupBy { release ->
		release.getMainMinecraftVersion() ?: extractMainMinecraftVersion(release.findNextMinecraftReleaseVersion())
	}
	.map { (mcVersion, releases) ->
		mcVersion to releases.size
	}
	.sortedByDescending { (version, _) ->
		// Sort by version number (newest first)
		version?.split(".")?.let { parts ->
			val major = parts.getOrNull(0)?.toIntOrNull() ?: 0
			val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
			major * 1000 + minor
		} ?: 0
	}

	Div({
		classes(ReleaseStatsStyle.container)
	}) {
		Div({
			classes(ReleaseStatsStyle.statsRow)
		}) {
			Div({
				classes(ReleaseStatsStyle.statCard)
			}) {
				MdiArchive()
				Div({
					classes(ReleaseStatsStyle.statContent)
				}) {
					Div({
						classes(ReleaseStatsStyle.statValue)
					}) {
						Text("$totalReleases")
					}
					Div({
						classes(ReleaseStatsStyle.statLabel)
					}) {
						Text("Total Releases")
					}
				}
			}

			Div({
				classes(ReleaseStatsStyle.statCard)
			}) {
				MdiCalendarMonth()
				Div({
					classes(ReleaseStatsStyle.statContent)
				}) {
					Div({
						classes(ReleaseStatsStyle.statValue)
					}) {
						Text(firstReleaseDate)
					}
					Div({
						classes(ReleaseStatsStyle.statLabel)
					}) {
						Text("First Release")
					}
				}
			}

			Div({
				classes(ReleaseStatsStyle.statCard)
			}) {
				MdiCalendarMonth()
				Div({
					classes(ReleaseStatsStyle.statContent)
				}) {
					Div({
						classes(ReleaseStatsStyle.statValue)
					}) {
						Text(latestReleaseDate)
					}
					Div({
						classes(ReleaseStatsStyle.statLabel)
					}) {
						Text("Latest Release")
					}
				}
			}
		}

		if (minecraftVersionStats.isNotEmpty()) {
			Div({
				classes(ReleaseStatsStyle.minecraftVersions)
			}) {
				H3({
					classes(ReleaseStatsStyle.sectionTitle)
				}) {
					Text("Releases by Minecraft Version")
				}

				Div({
					classes(ReleaseStatsStyle.minecraftVersionBars)
				}) {
					val maxReleases = minecraftVersionStats.maxOfOrNull { it.second } ?: 1

					minecraftVersionStats.forEach { (version, count) ->
						Div({
							classes(ReleaseStatsStyle.versionBarContainer)
						}) {
							Div({
								classes(ReleaseStatsStyle.versionLabel)
							}) {
								Text(version ?: "Unknown")
							}

							Div({
								classes(ReleaseStatsStyle.versionBarOuter)
							}) {
								Div({
									classes(ReleaseStatsStyle.versionBarInner)
									style {
										width((count.toDouble() / maxReleases.toDouble() * 100).percent)
									}
								}) {
									Text("$count")
								}
							}
						}
					}
				}
			}
		}
	}
}

object ReleaseStatsStyle : StyleSheet() {
	val container by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(2.cssRem)
		marginBottom(2.cssRem)
		width(100.percent)
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		borderRadius(GlobalStyle.roundingButton)
		padding(1.5.cssRem)

		mdMax(self) {
			padding(1.2.cssRem)
			gap(1.5.cssRem)
		}

		smMax(self) {
			padding(1.cssRem)
			gap(1.2.cssRem)
		}
	}

	val statsRow by style {
		display(DisplayStyle.Grid)
		gap(1.cssRem)
		gridTemplateColumns { repeat(3) { size(1.fr) } }
		width(100.percent)

		mdMax(self) {
			gridTemplateColumns { repeat(2) { size(1.fr) } }
		}

		smMax(self) {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			gap(0.8.cssRem)
		}
	}

	val statCard by style {
		alignItems(AlignItems.Center)
		backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		borderRadius(GlobalStyle.roundingButton)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		gap(0.8.cssRem)
		padding(1.cssRem)
		transition(0.3.s, "background-color", "translate", "box-shadow")

		hover(self) style {
			backgroundColor(GlobalStyle.tertiaryBackgroundColor.alpha(0.8))
			boxShadow(0.px, 4.px, 8.px, 0.px, rgba(0, 0, 0, 0.1))
			translateY((-2).px)
		}

		"span.material-icons" style {
			color(GlobalStyle.altTextColor)
			fontSize(2.cssRem)
		}

		mdMax(self) {
			padding(0.8.cssRem)
		}
	}

	val statContent by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
	}

	val statValue by style {
		fontSize(1.4.cssRem)
		fontWeight(700)

		mdMax(self) {
			fontSize(1.2.cssRem)
		}
	}

	val statLabel by style {
		color(GlobalStyle.altTextColor)
		fontSize(0.9.cssRem)
	}

	val minecraftVersions by style {
		backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		borderRadius(GlobalStyle.roundingButton)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
		padding(1.2.cssRem)

		mdMax(self) {
			padding(1.cssRem)
		}

		smMax(self) {
			padding(0.8.cssRem)
		}
	}

	val sectionTitle by style {
		fontSize(1.2.cssRem)
		margin(0.px)
		marginBottom(0.5.cssRem)
	}

	val minecraftVersionBars by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.8.cssRem)

		mdMax(self) {
			gap(0.6.cssRem)
		}
	}

	val versionBarContainer by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		alignItems(AlignItems.Center)
		gap(1.cssRem)

		mdMax(self) {
			gap(0.8.cssRem)
		}

		smMax(self) {
			gap(0.6.cssRem)
		}
	}

	val versionLabel by style {
		fontSize(0.9.cssRem)
		fontWeight(600)
		minWidth(4.cssRem)
		textAlign(TextAlign.Right)
	}

	val versionBarOuter by style {
		backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		borderRadius(GlobalStyle.roundingButton)
		flexGrow(1)
		height(1.8.cssRem)
		overflow(Overflow.Hidden)

		mdMax(self) {
			height(1.6.cssRem)
		}
	}

	val versionBarInner by style {
		alignItems(AlignItems.Center)
		backgroundColor(GlobalStyle.buttonBackgroundColor)
		color(GlobalStyle.textColor)
		display(DisplayStyle.Flex)
		fontSize(0.8.cssRem)
		fontWeight(600)
		height(100.percent)
		justifyContent(JustifyContent.FlexEnd)
		paddingRight(0.5.cssRem)
		transition(0.3.s, "background", "width")

		hover(self) style {
			backgroundColor(GlobalStyle.buttonBackgroundColorHover)
		}
	}
}
