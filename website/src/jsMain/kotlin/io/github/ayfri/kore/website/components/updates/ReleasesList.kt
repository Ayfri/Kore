package io.github.ayfri.kore.website.components.updates

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.components.icons.mdi.MdiCalendarMonth
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*

@Composable
fun ReleasesList(releases: List<GitHubRelease>) {
	Style(ReleasesListStyle)

	val allReleases = releases.sortedByDescending { it.publishedAt }

	var filterOptions by remember { mutableStateOf(ReleaseFilterOptions()) }

	val sortComparator by mutableStateOf(when (filterOptions.sortOrder) {
		SortOrder.NEWEST_FIRST -> compareByDescending<GitHubRelease> { it.publishedAt }
		SortOrder.OLDEST_FIRST -> compareBy<GitHubRelease> { it.publishedAt }
	})
	// Apply the filters
	val filteredReleases = allReleases.filter { release ->
		release.matchesFilters(filterOptions)
	}.sortedWith(sortComparator)

	Div({
		classes(ReleasesListStyle.container)
	}) {
		// Add the statistics
		ReleaseStats(allReleases, filteredReleases)

		// Add the filters
		ReleaseFilters(
			allReleases = allReleases,
			filterOptions = filterOptions,
			onFilterChange = { filterOptions = it }
		)

		Div({
			classes(ReleasesListStyle.releasesHeader)
		}) {
			P("Showing ${filteredReleases.size} out of ${allReleases.size} releases")
		}

		if (filteredReleases.isEmpty()) {
			Div({
				classes(ReleasesListStyle.emptyState)
			}) {
				H3 {
					Text("No releases match your filters")
				}
				P {
					Text("Try adjusting your search criteria or filters")
				}
			}
		} else {
			Div({
				classes(ReleasesListStyle.releasesGrid)
			}) {
				filteredReleases.forEach { release ->
					ReleaseCard(release)
				}
			}
		}
	}
}

@Composable
fun ReleaseCard(release: GitHubRelease) {
	Div({
		classes(ReleasesListStyle.releaseCard)
	}) {
		Div({
			classes(ReleasesListStyle.releaseHeader)
		}) {
			A(release.htmlUrl, {
				target(ATarget.Blank)
				rel("noopener", "noreferrer")
				classes(ReleasesListStyle.releaseLink)
			}) {
				H3({
					classes(ReleasesListStyle.releaseTitle)
				}) {
					Text(release.name)
				}
			}

			Div({
				classes(ReleasesListStyle.releaseMetadata)
			}) {
				Span {
					Text(release.tagName)
				}

				release.getMinecraftVersion()?.let { mcVersion ->
					Div({
						classes(ReleasesListStyle.minecraftVersion)
					}) {
						Text("MC $mcVersion")
					}
				}

				Span({
					classes(ReleasesListStyle.releaseDate)
				}) {
					MdiCalendarMonth()
					Text("Published ${formatDate(release.publishedAt)}")
					Text(" • ")
					Text("(${formatRelativeDate(release.publishedAt)})")
				}

				if (release.isPrerelease) {
					Div({
						classes(ReleasesListStyle.preReleaseTag)
					}) {
						Text("Pre-Release")
					}
				}
			}
		}

		if (release.body.isNotEmpty()) {
			Div({
				classes(ReleasesListStyle.releaseBody)
			}) {
				// Use a unique ID for each release markdown to avoid conflicts
				val markdownId = "release-body-${release.id}"
				MarkdownRenderer(release.body, markdownId)
			}
		}

		if (release.assets.isNotEmpty()) {
			Div({
				classes(ReleasesListStyle.assetsList)
			}) {
				H4({
					classes(ReleasesListStyle.assetsTitle)
				}) {
					Text("Downloads")
				}

				release.assets.forEach { asset ->
					A(asset.browserDownloadUrl, {
						classes(ReleasesListStyle.assetItem)
						target(ATarget.Blank)
						rel("noopener", "noreferrer")
					}) {
						Text(asset.name)
					}
				}
			}
		}
	}
}

object ReleasesListStyle : StyleSheet() {
	val container by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		width(100.percent)
	}

	val releasesHeader by style {
		color(GlobalStyle.altTextColor)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.RowReverse)
		fontSize(0.9.cssRem)
		fontStyle(FontStyle.Italic)
	}

	val releasesGrid by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(2.cssRem)
		width(100.percent)

		mdMax(self) {
			gap(1.5.cssRem)
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val releaseCard by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		borderRadius(GlobalStyle.roundingButton)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		padding(1.5.cssRem)
		transition(0.3.s, "background-color", "transform", "box-shadow")

		hover(self) style {
			console.log(GlobalStyle.tertiaryBackgroundColor.alpha(0.5))
			backgroundColor(GlobalStyle.tertiaryBackgroundColor.alpha(0.66))
			boxShadow(0.px, 4.px, 12.px, 0.px, rgba(0, 0, 0, 0.15))
			transform { translateY((-3).px) }
		}

		mdMax(self) {
			padding(1.2.cssRem)
		}

		smMax(self) {
			padding(1.cssRem)
			gap(0.8.cssRem)
		}
	}

	val releaseHeader by style {
		borderBottom(1.px, LineStyle.Solid, rgba(255, 255, 255, 0.1))
		paddingBottom(0.8.cssRem)
	}

	val releaseTitle by style {
		fontSize(1.5.cssRem)
		margin(0.px)
		marginBottom(0.5.cssRem)

		mdMax(self) {
			fontSize(1.3.cssRem)
		}
	}

	val releaseMetadata by style {
		alignItems(AlignItems.Center)
		color(GlobalStyle.altTextColor)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		fontSize(0.9.cssRem)
		gap(1.cssRem)

		mdMax(self) {
			gap(0.8.cssRem)
		}

		smMax(self) {
			gap(0.6.cssRem)
			fontSize(0.85.cssRem)
		}
	}

	val releaseDate by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		gap(0.5.cssRem)
	}

	val releaseBody by style {
		borderRadius(GlobalStyle.roundingButton)
		overflow(Overflow.Hidden)
		padding(0.px)
		width(100.percent)

		mdMax(self) {
			fontSize(0.95.cssRem)
		}
	}

	val minecraftVersion by style {
		backgroundColor(rgba(80, 175, 80, 0.2))
		borderRadius(GlobalStyle.roundingButton)
		color(rgb(80, 205, 80))
		fontSize(0.8.cssRem)
		fontWeight(600)
		padding(0.25.cssRem, 0.5.cssRem)

		hover(self) style {
			backgroundColor(rgba(80, 175, 80, 0.3))
		}
	}

	val preReleaseTag by style {
		backgroundColor(rgba(230, 180, 60, 0.2))
		borderRadius(GlobalStyle.roundingButton)
		color(rgb(230, 180, 60))
		fontSize(0.8.cssRem)
		fontWeight(600)
		padding(0.25.cssRem, 0.5.cssRem)

		hover(self) style {
			backgroundColor(rgba(230, 180, 60, 0.3))
		}
	}

	val assetsList by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.5.cssRem)
	}

	val assetsTitle by style {
		fontSize(1.1.cssRem)
		margin(0.px)
		marginBottom(0.6.cssRem)
		fontWeight(600)
	}

	val assetItem by style {
		backgroundColor(rgba(255, 255, 255, 0.05))
		borderRadius(GlobalStyle.roundingButton)
		color(GlobalStyle.textColor)
		display(DisplayStyle.Block)
		padding(0.5.cssRem, 0.8.cssRem)
		textDecoration("none")
		transition(0.2.s, "background-color", "box-shadow")

		hover(self) style {
			backgroundColor(rgba(255, 255, 255, 0.1))
			boxShadow(0.px, 2.px, 4.px, 0.px, rgba(0, 0, 0, 0.1))
			color(GlobalStyle.linkColorHover)
		}
	}

	val emptyState by style {
		alignItems(AlignItems.Center)
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		borderRadius(GlobalStyle.roundingButton)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.8.cssRem)
		justifyContent(JustifyContent.Center)
		padding(3.cssRem)
		textAlign(TextAlign.Center)

		"h3" style {
			color(GlobalStyle.textColor)
			fontSize(1.5.cssRem)
			margin(0.px)
		}

		"p" style {
			color(GlobalStyle.altTextColor)
			fontSize(1.cssRem)
			margin(0.px)
		}

		mdMax(self) {
			padding(2.5.cssRem)

			"h3" style {
				fontSize(1.3.cssRem)
			}
		}
	}

	val releaseLink by style {
		color(GlobalStyle.textColor)
		textDecoration("none")
		transition(0.2.s, "color")

		hover(self) style {
			color(GlobalStyle.linkColorHover)
		}
	}
}
