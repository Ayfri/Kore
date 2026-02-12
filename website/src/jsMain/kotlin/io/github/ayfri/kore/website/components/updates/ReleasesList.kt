package io.github.ayfri.kore.website.components.updates

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.storage.BooleanStorageKey
import com.varabyte.kobweb.browser.storage.getItem
import com.varabyte.kobweb.browser.storage.setItem
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.components.icons.mdi.MdiCalendarMonth
import com.varabyte.kobweb.silk.components.icons.mdi.MdiOpenInNew
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.*
import kotlinx.browser.localStorage
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*

private val ShowPreReleasesStorageKey = BooleanStorageKey("kore.releases.showPreReleases", defaultValue = false)
private val ShowSnapshotsStorageKey = BooleanStorageKey("kore.releases.showSnapshots", defaultValue = false)
private val ShowReleaseCandidatesStorageKey = BooleanStorageKey("kore.releases.showReleaseCandidates", defaultValue = false)

@Composable
fun ReleasesList(releases: List<GitHubRelease>) {
	Style(ReleasesListStyle)

	val allReleases = releases.sortedByDescending { it.publishedAt }
	val initialFilterOptions = remember {
		ReleaseFilterOptions(
			showPreReleases = localStorage.getItem(ShowPreReleasesStorageKey) ?: false,
			showSnapshots = localStorage.getItem(ShowSnapshotsStorageKey) ?: false,
			showReleaseCandidates = localStorage.getItem(ShowReleaseCandidatesStorageKey) ?: false
		)
	}
	var filterOptions by remember { mutableStateOf(initialFilterOptions) }

	val sortComparator by mutableStateOf(
		when (filterOptions.sortOrder) {
		SortOrder.NEWEST_FIRST -> compareByDescending<GitHubRelease> { it.publishedAt }
			SortOrder.OLDEST_FIRST -> compareBy { it.publishedAt }
	})
	// Apply the filters
	val filteredReleases = allReleases.filter { release ->
		release.matchesFilters(filterOptions)
	}.sortedWith(sortComparator)

	Div({
		classes(ReleasesListStyle.container)
	}) {
		// Add the filters
		ReleaseFilters(
			allReleases = allReleases, filterOptions = filterOptions, onFilterChange = { newOptions ->
				if (newOptions.showPreReleases != filterOptions.showPreReleases) {
					localStorage.setItem(ShowPreReleasesStorageKey, newOptions.showPreReleases)
				}
				if (newOptions.showSnapshots != filterOptions.showSnapshots) {
					localStorage.setItem(ShowSnapshotsStorageKey, newOptions.showSnapshots)
				}
				if (newOptions.showReleaseCandidates != filterOptions.showReleaseCandidates) {
					localStorage.setItem(ShowReleaseCandidatesStorageKey, newOptions.showReleaseCandidates)
				}
				filterOptions = newOptions
			})

		Div({
			id("changelogs")
			classes(ReleasesListStyle.releasesHeader)
		}) {
			H2({
				classes(ReleasesListStyle.releasesTitle)
			}) {
				Text("Changelogs")
			}
			P({
				classes(ReleasesListStyle.releasesCount)
			}) {
				Text("Showing ${filteredReleases.size} out of ${allReleases.size} releases")
			}
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

		// Add the statistics
		ReleaseStats(allReleases, filteredReleases)
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
			Div({
				classes(ReleasesListStyle.releaseHeaderMain)
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

			buildMinecraftChangelogUrl(release)?.let { changelogUrl ->
				A(changelogUrl, {
					classes(ReleasesListStyle.minecraftChangelogLink)
					target(ATarget.Blank)
					rel("noopener", "noreferrer")
				}) {
					MdiOpenInNew()
					Text("Minecraft changelog")
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

private fun buildMinecraftChangelogUrl(release: GitHubRelease): String? {
	val mcVersion = release.getMinecraftVersion() ?: return null
	val baseVersion = mcVersion.substringBefore("-")
	val normalizedBase = baseVersion.replace(".", "-")

	return when {
		release.isSnapshot() -> "https://www.minecraft.net/en-us/article/minecraft-snapshot-$mcVersion"

		release.isPreRelease() -> {
			val preNumber = mcVersion.substringAfter("-pre", "")
			if (preNumber.isEmpty()) null
			else "https://www.minecraft.net/en-us/article/minecraft-$normalizedBase-pre-release-$preNumber"
		}

		release.isReleaseCandidate() -> {
			val rcNumber = mcVersion.substringAfter("-rc", "")
			if (rcNumber.isEmpty()) null
			else "https://www.minecraft.net/en-us/article/minecraft-$normalizedBase-release-candidate-$rcNumber"
		}

		release.isRelease() -> "https://www.minecraft.net/en-us/article/minecraft-java-edition-$normalizedBase"
		else -> null
	}
}

object ReleasesListStyle : StyleSheet() {
	val container by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		width(100.percent)
	}

	val releasesHeader by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		justifyContent(JustifyContent.SpaceBetween)
		marginBottom(1.2.cssRem)
		scrollMarginTop(6.cssRem)

		mdMax(self) {
			alignItems(AlignItems.FlexStart)
			flexDirection(FlexDirection.Column)
			gap(0.4.cssRem)
		}
	}

	val releasesTitle by style {
		fontSize(1.5.cssRem)
		margin(0.px)
		fontWeight(700)
	}

	val releasesCount by style {
		color(GlobalStyle.altTextColor)
		fontSize(0.9.cssRem)
		fontStyle(FontStyle.Italic)
		margin(0.px)
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
		alignItems(AlignItems.FlexStart)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		justifyContent(JustifyContent.SpaceBetween)
		gap(1.cssRem)
		borderBottom(1.px, LineStyle.Solid, rgba(255, 255, 255, 0.1))
		paddingBottom(0.8.cssRem)

		mdMax(self) {
			alignItems(AlignItems.FlexStart)
			flexDirection(FlexDirection.Column)
		}
	}

	val releaseHeaderMain by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.6.cssRem)
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

	val minecraftChangelogLink by style {
		alignItems(AlignItems.Center)
		color(GlobalStyle.linkColor)
		display(DisplayStyle.Flex)
		fontSize(0.85.cssRem)
		fontWeight(600)
		gap(0.35.cssRem)
		textDecorationLine(TextDecorationLine.None)
		padding(0.1.cssRem, 0.4.cssRem)
		borderRadius(GlobalStyle.roundingButton)
		backgroundColor(GlobalStyle.buttonBackgroundColor.alpha(0.12))
		transition(0.2.s, "background-color", "color")

		"span.material-icons" style {
			fontSize(1.05.cssRem)
		}

		hover(self) style {
			backgroundColor(GlobalStyle.buttonBackgroundColor.alpha(0.25))
			color(GlobalStyle.linkColorHover)
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
		textDecorationLine(TextDecorationLine.None)
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
		textDecorationLine(TextDecorationLine.None)
		transition(0.2.s, "color")

		hover(self) style {
			color(GlobalStyle.linkColorHover)
		}
	}
}
