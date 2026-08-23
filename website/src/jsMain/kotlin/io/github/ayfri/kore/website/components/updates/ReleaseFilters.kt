package io.github.ayfri.kore.website.components.updates

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.components.icons.mdi.MdiFilter
import com.varabyte.kobweb.silk.components.icons.mdi.MdiRestartAlt
import com.varabyte.kobweb.silk.components.icons.mdi.MdiSearch
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.*
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
	val showPreReleases: Boolean = false,
	val showSnapshots: Boolean = false,
	val showReleaseCandidates: Boolean = false,
	val selectedMinecraftVersions: Set<String> = emptySet(),
	val sortOrder: SortOrder = SortOrder.NEWEST_FIRST,
) {
	/** Lowercased once per query instead of once per release tested. */
	val lowercasedSearchQuery by lazy { searchQuery.lowercase() }
}

enum class SortOrder(val label: String, val inputId: String) {
	NEWEST_FIRST("Newest First", "sort-newest"),
	OLDEST_FIRST("Oldest First", "sort-oldest"),
}

/** Version buckets and badge counts derived from the release list, built once per list instead of per recomposition. */
private class ReleaseFacets(releases: List<GitHubRelease>) {
	private val baseVersions = releases.mapNotNull { it.baseMinecraftVersion }
	private val baseCounts = baseVersions.groupingBy { it }.eachCount()
	private val mainCounts = baseVersions.mapNotNull { extractMainMinecraftVersion(it) }.groupingBy { it }.eachCount()

	val snapshots = releases.count { it.isSnapshot }
	val preReleases = releases.count { it.isPreReleaseVersion }
	val releaseCandidates = releases.count { it.isReleaseCandidate }

	/** A `major.minor` bucket paired with itself followed by its `major.minor.patch` versions, newest first. */
	val versionGroups = baseCounts.keys
		.groupBy { extractMainMinecraftVersion(it) }
		.mapNotNull { (main, bases) ->
			if (main == null) return@mapNotNull null
			val patches = bases.filterNot { it == main }.sortedWith { left, right -> compareMinecraftVersions(right, left) }
			main to (listOf(main) + patches)
		}
		.sortedWith { (left), (right) -> compareMinecraftVersions(right, left) }

	fun countOf(version: String, mainVersion: String) =
		(if (version == mainVersion) mainCounts[mainVersion] else baseCounts[version]) ?: 0
}

@Composable
private fun FilterGroup(label: String, vararg extraClasses: String, content: @Composable () -> Unit) {
	Div({
		classes(ReleaseFiltersStyle.filterGroup, *extraClasses)
	}) {
		Div({
			classes(ReleaseFiltersStyle.filterLabel)
		}) {
			Text(label)
		}

		content()
	}
}

@Composable
private fun FilterCheckbox(id: String, label: String, count: Int, checked: Boolean, onChange: (Boolean) -> Unit) {
	Div({
		classes(ReleaseFiltersStyle.checkboxContainer)
	}) {
		Input(InputType.Checkbox) {
			id(id)
			checked(checked)
			onChange { onChange(it.value) }
		}

		Label(id, {
			classes(ReleaseFiltersStyle.checkboxLabel)
		}) {
			Text(label)
			Span({
				classes(ReleaseFiltersStyle.badge)
			}) {
				Text("$count")
			}
		}
	}
}

@Composable
private fun SortRadio(order: SortOrder, checked: Boolean, onSelect: () -> Unit) {
	Div({
		classes(ReleaseFiltersStyle.radioContainer)
	}) {
		Input(InputType.Radio) {
			id(order.inputId)
			name("sort-order")
			checked(checked)
			onChange { onSelect() }
		}

		Label(order.inputId) {
			Text(order.label)
		}
	}
}

@Composable
private fun VersionChip(version: String, count: Int, selected: Boolean, onToggle: () -> Unit) {
	Div({
		classes(ReleaseFiltersStyle.versionChip)
		if (selected) classes(ReleaseFiltersStyle.versionChipSelected)
		onClick { onToggle() }
	}) {
		Text(version)
		Span({
			classes(ReleaseFiltersStyle.versionChipCount)
		}) {
			Text("($count)")
		}
	}
}

@Composable
fun ReleaseFilters(
	allReleases: List<GitHubRelease>,
	filterOptions: ReleaseFilterOptions,
	onFilterChange: (ReleaseFilterOptions) -> Unit,
) {
	Style(ReleaseFiltersStyle)

	var expanded by remember { mutableStateOf(false) }
	val facets = remember(allReleases) { ReleaseFacets(allReleases) }

	Div({
		id("filters")
		classes(ReleaseFiltersStyle.container)
	}) {
		Div({
			classes(ReleaseFiltersStyle.searchBar)
		}) {
			Div({
				classes(ReleaseFiltersStyle.searchInputContainer)
			}) {
				MdiSearch()
				Input(InputType.Text) {
					classes(ReleaseFiltersStyle.searchInput)
					id("release-search")
					placeholder("Search by name, tag, version, or content...")
					value(filterOptions.searchQuery)
					onInput { onFilterChange(filterOptions.copy(searchQuery = it.value)) }
				}
			}

			Button({
				classes(ReleaseFiltersStyle.filterToggle)
				attr("aria-expanded", "$expanded")
				attr("aria-controls", "filters-panel")
				onClick { expanded = !expanded }
			}) {
				MdiFilter()
				Text(if (expanded) "Hide Filters" else "Show Filters")
			}

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

		// The panel is a single-row grid animating between 0fr and 1fr, so the height follows the real content.
		Div({
			id("filters-panel")
			classes(ReleaseFiltersStyle.filtersPanel)
			if (!expanded) {
				classes(ReleaseFiltersStyle.filtersPanelCollapsed)
				attr("inert", "")
			}
		}) {
			Div({
				classes(ReleaseFiltersStyle.filters)
			}) {
				FilterGroup("Release Type") {
					Div({
						classes(ReleaseFiltersStyle.checkboxGroup)
					}) {
						FilterCheckbox(
							id = "show-release-candidates",
							label = "Show Release Candidates",
							count = facets.releaseCandidates,
							checked = filterOptions.showReleaseCandidates,
						) { onFilterChange(filterOptions.copy(showReleaseCandidates = it)) }

						FilterCheckbox(
							id = "show-prereleases",
							label = "Show Pre-releases",
							count = facets.preReleases,
							checked = filterOptions.showPreReleases,
						) { onFilterChange(filterOptions.copy(showPreReleases = it)) }

						FilterCheckbox(
							id = "show-snapshots",
							label = "Show Snapshots",
							count = facets.snapshots,
							checked = filterOptions.showSnapshots,
						) { onFilterChange(filterOptions.copy(showSnapshots = it)) }
					}
				}

				FilterGroup("Sort Order") {
					Div({
						classes(ReleaseFiltersStyle.radioGroup)
					}) {
						SortOrder.entries.forEach { order ->
							SortRadio(order, filterOptions.sortOrder == order) {
								onFilterChange(filterOptions.copy(sortOrder = order))
							}
						}
					}
				}

				if (facets.versionGroups.isNotEmpty()) {
					FilterGroup("Minecraft Versions", ReleaseFiltersStyle.versionsFilter) {
						Div({
							classes(ReleaseFiltersStyle.versionsList)
						}) {
							facets.versionGroups.forEach { (mainVersion, versions) ->
								Div({
									classes(ReleaseFiltersStyle.versionsRow)
								}) {
									versions.forEach { version ->
										val selected = version in filterOptions.selectedMinecraftVersions

										VersionChip(version, facets.countOf(version, mainVersion), selected) {
											val current = filterOptions.selectedMinecraftVersions
											onFilterChange(
												filterOptions.copy(
													selectedMinecraftVersions = if (selected) current - version else current + version
												)
											)
										}
									}
								}
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
		boxShadow(0.px, 2.px, 6.px, 0.px, rgba(0, 0, 0, 0.1))
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		marginBottom(1.6.cssRem)
		padding(1.3.cssRem)
		scrollMarginTop(6.cssRem)
		width(100.percent)

		mdMax(self) {
			padding(1.1.cssRem)
			gap(0.9.cssRem)
		}

		smMax(self) {
			padding(0.95.cssRem)
			gap(0.75.cssRem)
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
		outlineWidth(0.px)
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
			boxShadow(0.px, 2.px, 4.px, 0.px, rgba(0, 0, 0, 0.1))
		}

		"span.material-icons" style {
			fontSize(1.2.cssRem)
		}

		mdMax(self) {
			alignSelf(AlignSelf.Stretch)
			justifyContent(JustifyContent.Center)
		}
	}

	val filtersPanel by style {
		display(DisplayStyle.Grid)
		gridTemplateRows { size(1.fr) }
		opacity(1)
		transition(0.22.s, "grid-template-rows", "opacity")
	}

	val filtersPanelCollapsed by style {
		gridTemplateRows { size(0.fr) }
		opacity(0)
	}

	val filters by style {
		display(DisplayStyle.Grid)
		gap(1.5.cssRem)
		gridTemplateColumns { repeat(3) { size(1.fr) } }
		minHeight(0.px)
		overflow(Overflow.Hidden)
		paddingTop(1.2.cssRem)

		mdMax(self) {
			gridTemplateColumns { repeat(2) { size(1.fr) } }
			gap(1.2.cssRem)
		}

		smMax(self) {
			gridTemplateColumns { size(1.fr) }
			gap(1.cssRem)
			paddingTop(1.cssRem)
		}
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

	val versionsList by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.8.cssRem)
	}

	val versionsRow by style {
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

	val versionChipCount by style {
		color(GlobalStyle.altTextColor)
		fontSize(0.7.cssRem)
		fontWeight(500)
		marginLeft(0.35.cssRem)
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
