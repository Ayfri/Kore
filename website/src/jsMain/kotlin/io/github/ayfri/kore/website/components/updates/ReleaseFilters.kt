package io.github.ayfri.kore.website.components.updates

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.components.icons.mdi.MdiFilter
import com.varabyte.kobweb.silk.components.icons.mdi.MdiRestartAlt
import com.varabyte.kobweb.silk.components.icons.mdi.MdiSearch
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.alpha
import io.github.ayfri.kore.website.utils.extractMainMinecraftVersion
import io.github.ayfri.kore.website.utils.extractMinecraftVersion
import io.github.ayfri.kore.website.utils.isSnapshotVersion
import io.github.ayfri.kore.website.utils.mdMax
import io.github.ayfri.kore.website.utils.placeholder
import io.github.ayfri.kore.website.utils.smMax
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.AlignSelf
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*

/**
 * Filtering options for GitHub releases
 */
data class ReleaseFilterOptions(
	val searchQuery: String = "",
	val showPreReleases: Boolean = true,
	val showSnapshots: Boolean = true,
	val selectedMinecraftVersions: Set<String> = emptySet(),
	val sortOrder: SortOrder = SortOrder.NEWEST_FIRST
)

enum class SortOrder {
	NEWEST_FIRST,
	OLDEST_FIRST
}

@OptIn(ExperimentalComposeWebApi::class)
@Composable
fun ReleaseFilters(
	allReleases: List<GitHubRelease>,
	filterOptions: ReleaseFilterOptions,
	onFilterChange: (ReleaseFilterOptions) -> Unit
) {
	Style(ReleaseFiltersStyle)

	var expanded by remember { mutableStateOf(false) }

	// Extract all available Minecraft versions in the releases
	val availableMinecraftVersions = allReleases.mapNotNull { release ->
		extractMinecraftVersion(release.tagName)?.let { mcVersion ->
			extractMainMinecraftVersion(mcVersion)
		}
	}.toSet().sortedByDescending { it }

	// Count snapshots for badge display
	val snapshotCount = allReleases.count { release ->
		val mcVersion = extractMinecraftVersion(release.tagName)
		isSnapshotVersion(mcVersion)
	}

	// Count prereleases for badge display
	val prereleaseCount = allReleases.count { it.isPrerelease }

	Div({
		classes(ReleaseFiltersStyle.container)
	}) {
		// Search bar
		Div({
			classes(ReleaseFiltersStyle.searchBar)
		}) {
			Label("release-search", {
				classes(ReleaseFiltersStyle.searchLabel)
			}) {
				Text("Search releases:")
			}

			Div({
				classes(ReleaseFiltersStyle.searchInputContainer)
			}) {
				MdiSearch()
				Input(InputType.Text) {
					classes(ReleaseFiltersStyle.searchInput)
					id("release-search")
					placeholder("Search by name, tag, version, or content...")
					value(filterOptions.searchQuery)
					onInput { event ->
						onFilterChange(filterOptions.copy(searchQuery = event.value))
					}
				}
			}

			Button({
				classes(ReleaseFiltersStyle.filterToggle)
				onClick { expanded = !expanded }
			}) {
				MdiFilter()
				Text(if (expanded) "Hide Filters" else "Show Filters")
			}

			// Reset button
			if (filterOptions != ReleaseFilterOptions()) {
				Button({
					classes(ReleaseFiltersStyle.resetButton)
					onClick { onFilterChange(ReleaseFilterOptions()) }
				}) {
					MdiRestartAlt()
					Text("Reset Filters")
				}
			}
		}

		// Advanced filters (collapsible)
		Div({
			classes(ReleaseFiltersStyle.filters)
			if (!expanded) classes(ReleaseFiltersStyle.filtersCollapsed)
		}) {
			// Pre-release filter
			Div({
				classes(ReleaseFiltersStyle.filterGroup)
			}) {
				Div({
					classes(ReleaseFiltersStyle.filterLabel)
				}) {
					Text("Release Type")
				}

				Div({
					classes(ReleaseFiltersStyle.checkboxGroup)
				}) {
					Div({
						classes(ReleaseFiltersStyle.checkboxContainer)
					}) {
						Input(InputType.Checkbox) {
							id("show-prereleases")
							checked(filterOptions.showPreReleases)
							onChange { event ->
								onFilterChange(filterOptions.copy(showPreReleases = event.value))
							}
						}

						Label("show-prereleases", {
							classes(ReleaseFiltersStyle.checkboxLabel)
						}) {
							Text("Show Pre-releases")
							Span({
								classes(ReleaseFiltersStyle.badge)
							}) {
								Text(prereleaseCount.toString())
							}
						}
					}

					Div({
						classes(ReleaseFiltersStyle.checkboxContainer)
					}) {
						Input(InputType.Checkbox) {
							id("show-snapshots")
							checked(filterOptions.showSnapshots)
							onChange { event ->
								onFilterChange(filterOptions.copy(showSnapshots = event.value))
							}
						}

						Label("show-snapshots", {
							classes(ReleaseFiltersStyle.checkboxLabel)
						}) {
							Text("Show Snapshots")
							Span({
								classes(ReleaseFiltersStyle.badge)
							}) {
								Text(snapshotCount.toString())
							}
						}
					}
				}
			}

			// Sort filter
			Div({
				classes(ReleaseFiltersStyle.filterGroup)
			}) {
				Div({
					classes(ReleaseFiltersStyle.filterLabel)
				}) {
					Text("Sort Order")
				}

				Div({
					classes(ReleaseFiltersStyle.radioGroup)
				}) {
					Div({
						classes(ReleaseFiltersStyle.radioContainer)
					}) {
						Input(InputType.Radio) {
							id("sort-newest")
							name("sort-order")
							checked(filterOptions.sortOrder == SortOrder.NEWEST_FIRST)
							onChange {
								onFilterChange(filterOptions.copy(sortOrder = SortOrder.NEWEST_FIRST))
							}
						}

						Label("sort-newest") {
							Text("Newest First")
						}
					}

					Div({
						classes(ReleaseFiltersStyle.radioContainer)
					}) {
						Input(InputType.Radio) {
							id("sort-oldest")
							name("sort-order")
							checked(filterOptions.sortOrder == SortOrder.OLDEST_FIRST)
							onChange {
								onFilterChange(filterOptions.copy(sortOrder = SortOrder.OLDEST_FIRST))
							}
						}

						Label("sort-oldest") {
							Text("Oldest First")
						}
					}
				}
			}

			// Minecraft version filters (checkboxes)
			if (availableMinecraftVersions.isNotEmpty()) {
				Div({
					classes(ReleaseFiltersStyle.filterGroup, ReleaseFiltersStyle.versionsFilter)
				}) {
					Div({
						classes(ReleaseFiltersStyle.filterLabel)
					}) {
						Text("Minecraft Versions")
					}

					Div({
						classes(ReleaseFiltersStyle.versionsGrid)
					}) {
						availableMinecraftVersions.forEach { version ->
							val isChecked = filterOptions.selectedMinecraftVersions.contains(version)
							val versionId = "version-$version"

							Div({
								classes(ReleaseFiltersStyle.versionChip)
								if (isChecked) classes(ReleaseFiltersStyle.versionChipSelected)
								onClick {
									val newSelectedVersions = if (isChecked) {
										filterOptions.selectedMinecraftVersions - version
									} else {
										filterOptions.selectedMinecraftVersions + version
									}
									onFilterChange(filterOptions.copy(selectedMinecraftVersions = newSelectedVersions))
								}
							}) {
								Text(version)
							}
						}
					}
				}
			}
		}
	}
}

object ReleaseFiltersStyle : StyleSheet() {
	val container by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		borderRadius(GlobalStyle.roundingButton)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.2.cssRem)
		marginBottom(2.cssRem)
		padding(1.5.cssRem)
		transition(0.3.s, "padding")
		width(100.percent)
		boxShadow(0.px, 2.px, 6.px, 0.px, rgba(0, 0, 0, 0.1))

		mdMax(self) {
			padding(1.2.cssRem)
			gap(1.cssRem)
		}

		smMax(self) {
			padding(1.cssRem)
			gap(0.8.cssRem)
		}
	}

	val searchBar by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		flexWrap(FlexWrap.Wrap)
		gap(1.cssRem)
		width(100.percent)

		mdMax(self) {
			flexDirection(FlexDirection.Column)
			alignItems(AlignItems.Stretch)
			gap(0.8.cssRem)
		}
	}

	val searchLabel by style {
		fontWeight(600)
		whiteSpace(WhiteSpace.NoWrap)
		color(GlobalStyle.altTextColor)
		fontSize(0.95.cssRem)

		mdMax(self) {
			alignSelf(org.jetbrains.compose.web.css.AlignSelf.FlexStart)
		}
	}

	val searchInputContainer by style {
		alignItems(AlignItems.Center)
		backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		borderRadius(GlobalStyle.roundingButton)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		flexGrow(1)
		gap(0.5.cssRem)
		padding(0.px, 0.8.cssRem)
		transition(0.2.s, "background-color", "box-shadow")

		"span.material-icons" style {
			color(GlobalStyle.altTextColor)
			fontSize(1.5.cssRem)
		}

		hover(self) style {
			backgroundColor(rgba(255, 255, 255, 0.08))
		}

		self + focus style {
			boxShadow(0.px, 0.px, 0.px, 2.px, GlobalStyle.buttonBackgroundColor.alpha(0.5))
		}
	}

	val searchInput by style {
		backgroundColor(Color.transparent)
		border(0.px)
		color(GlobalStyle.textColor)
		flexGrow(1)
		fontSize(1.cssRem)
		height(2.5.cssRem)
		outline(0.px)
		width(100.percent)

		self + placeholder style {
			color(GlobalStyle.altTextColor)
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val filterToggle by style {
		alignItems(AlignItems.Center)
		backgroundColor(GlobalStyle.buttonBackgroundColor)
		border(0.px)
		borderRadius(GlobalStyle.roundingButton)
		color(GlobalStyle.textColor)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		fontSize(0.9.cssRem)
		fontWeight(600)
		gap(0.5.cssRem)
		padding(0.6.cssRem, 1.2.cssRem)
		transition(0.2.s, "background-color", "transform", "box-shadow")
		whiteSpace(WhiteSpace.NoWrap)

		hover(self) style {
			backgroundColor(GlobalStyle.buttonBackgroundColorHover)
			transform { translateY((-1).px) }
			boxShadow(0.px, 2.px, 6.px, 0.px, rgba(0, 0, 0, 0.2))
		}

		"span.material-icons" style {
			fontSize(1.2.cssRem)
		}

		mdMax(self) {
			alignSelf(AlignSelf.Stretch)
			justifyContent(JustifyContent.Center)
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val resetButton by style {
		alignItems(AlignItems.Center)
		backgroundColor(rgba(200, 80, 80, 0.2))
		border(0.px)
		borderRadius(GlobalStyle.roundingButton)
		color(rgb(230, 120, 120))
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		fontSize(0.9.cssRem)
		fontWeight(600)
		gap(0.5.cssRem)
		justifySelf(JustifySelf.Center)
		padding(0.6.cssRem, 1.2.cssRem)
		transition(0.2.s, "background-color", "transform", "box-shadow")
		whiteSpace(WhiteSpace.NoWrap)

		hover(self) style {
			backgroundColor(rgba(200, 80, 80, 0.3))
			transform { translateY((-1).px) }
			boxShadow(0.px, 2.px, 6.px, 0.px, rgba(0, 0, 0, 0.1))
		}

		"span.material-icons" style {
			fontSize(1.2.cssRem)
		}

		mdMax(self) {
			alignSelf(AlignSelf.Stretch)
			justifyContent(JustifyContent.Center)
		}
	}

	val filters by style {
		borderTop(1.px, LineStyle.Solid, rgba(255, 255, 255, 0.1))
		display(DisplayStyle.Grid)
		gap(1.5.cssRem)
		gridTemplateColumns { repeat(3) { size(1.fr) } }
		marginTop(0.8.cssRem)
		maxHeight(500.px)
		opacity(1)
		overflow(Overflow.Hidden)
		paddingTop(1.5.cssRem)
		transition(0.5.s, "max-height", "opacity", "padding-top", "margin-top", "border-top")

		mdMax(self) {
			gridTemplateColumns { repeat(2) { size(1.fr) } }
			gap(1.2.cssRem)
		}

		smMax(self) {
			gridTemplateColumns { size(1.fr) }
			gap(1.cssRem)
			paddingTop(1.2.cssRem)
		}
	}

	val filtersCollapsed by style {
		borderTop(0.px, LineStyle.Solid, rgba(255, 255, 255, 0))
		marginTop(0.px)
		maxHeight(0.px)
		opacity(0)
		paddingTop(0.px)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val filterGroup by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.8.cssRem)
		backgroundColor(GlobalStyle.tertiaryBackgroundColor.alpha(0.3))
		borderRadius(GlobalStyle.roundingButton)
		padding(1.cssRem)
		transition(0.2.s, "background-color", "transform", "box-shadow")

		hover(self) style {
			backgroundColor(GlobalStyle.tertiaryBackgroundColor.alpha(0.4))
			transform { translateY((-2).px) }
			boxShadow(0.px, 3.px, 8.px, 0.px, rgba(0, 0, 0, 0.15))
		}

		mdMax(self) {
			padding(0.8.cssRem)
		}
	}

	val filterLabel by style {
		color(GlobalStyle.altTextColor)
		fontSize(0.9.cssRem)
		fontWeight(600)
		textTransform(TextTransform.Uppercase)
		marginBottom(0.2.cssRem)
	}

	val checkboxGroup by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.8.cssRem)
	}

	val checkboxContainer by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		gap(0.5.cssRem)
	}

	val checkboxLabel by style {
		cursor(Cursor.Pointer)
		fontSize(0.95.cssRem)
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		gap(0.5.cssRem)
	}

	val badge by style {
		backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		borderRadius(16.px)
		color(GlobalStyle.textColor)
		fontSize(0.75.cssRem)
		fontWeight(700)
		lineHeight(1.0.number)
		minWidth(1.5.cssRem)
		padding(0.25.cssRem, 0.5.cssRem)
		textAlign(TextAlign.Center)
	}

	val radioGroup by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.8.cssRem)
	}

	val radioContainer by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		gap(0.5.cssRem)

		"label" style {
			cursor(Cursor.Pointer)
			fontSize(0.95.cssRem)
		}
	}

	val versionsFilter by style {
		gridColumn("span 3")

		mdMax(self) {
			gridColumn("span 2")
		}

		smMax(self) {
			gridColumn("span 1")
		}
	}

	val versionsGrid by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.6.cssRem)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val versionChip by style {
		alignItems(AlignItems.Center)
		backgroundColor(rgba(80, 80, 80, 0.3))
		borderRadius(20.px)
		color(GlobalStyle.altTextColor)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		fontSize(0.9.cssRem)
		fontWeight(500)
		justifyContent(JustifyContent.Center)
		padding(0.4.cssRem, 0.8.cssRem)
		transition(0.2.s, "background-color", "color", "transform", "box-shadow")
		userSelect(UserSelect.None)

		hover(self) style {
			backgroundColor(rgba(100, 100, 100, 0.4))
			transform { translateY((-1).px) }
			boxShadow(0.px, 2.px, 4.px, 0.px, rgba(0, 0, 0, 0.2))
		}
	}

	val versionChipSelected by style {
		backgroundColor(GlobalStyle.buttonBackgroundColor)
		color(GlobalStyle.textColor)
		fontWeight(600)

		hover(self) style {
			backgroundColor(GlobalStyle.buttonBackgroundColorHover)
		}
	}
}
