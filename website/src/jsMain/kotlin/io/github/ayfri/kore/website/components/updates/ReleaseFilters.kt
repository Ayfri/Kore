package io.github.ayfri.kore.website.components.updates

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.components.icons.lucide.LucideFunnel
import com.varabyte.kobweb.silk.components.icons.lucide.LucideRotateCcw
import com.varabyte.kobweb.silk.components.icons.lucide.LucideSearch
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
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

	val activeCount get() = listOf(showPreReleases, showSnapshots, showReleaseCandidates).count { it } + selectedMinecraftVersions.size
}

enum class SortOrder(val label: String) {
	NEWEST_FIRST("Newest"),
	OLDEST_FIRST("Oldest"),
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
private fun FilterGroup(label: String, content: @Composable () -> Unit) {
	Div({ classes(ReleaseFiltersStyle.group) }) {
		Span(label, ReleaseFiltersStyle.groupLabel)
		content()
	}
}

@Composable
private fun ChannelSwitch(label: String, count: Int, checked: Boolean, onChange: (Boolean) -> Unit) {
	Label(attrs = { classes(ReleaseFiltersStyle.switchRow) }) {
		Span(label)
		Span("$count", ReleaseFiltersStyle.count)
		Input(InputType.Checkbox) {
			classes(ReleaseFiltersStyle.switch)
			checked(checked)
			onChange { onChange(it.value) }
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

	Aside({
		id("filters")
		classes(ReleaseFiltersStyle.sidebar)
	}) {
		Div({ classes(ReleaseFiltersStyle.search) }) {
			LucideSearch()
			Input(InputType.Search) {
				id("release-search")
				placeholder("Search releases...")
				value(filterOptions.searchQuery)
				onInput { onFilterChange(filterOptions.copy(searchQuery = it.value)) }
			}
		}

		Button({
			classes(ReleaseFiltersStyle.panelToggle)
			attr("aria-expanded", "$expanded")
			attr("aria-controls", "filters-panel")
			onClick { expanded = !expanded }
		}) {
			LucideFunnel()
			Text(if (expanded) "Hide filters" else "Filters")
			filterOptions.activeCount.takeIf { it > 0 }?.let { Span("$it", ReleaseFiltersStyle.count) }
		}

		// A single-row grid animating between 0fr and 1fr, so the height follows the content. Only collapses under lg.
		Div({
			id("filters-panel")
			classes(ReleaseFiltersStyle.panel)
			if (!expanded) classes(ReleaseFiltersStyle.panelCollapsed)
		}) {
			Div({ classes(ReleaseFiltersStyle.panelContent) }) {
				FilterGroup("Channels") {
					ChannelSwitch("Release candidates", facets.releaseCandidates, filterOptions.showReleaseCandidates) {
						onFilterChange(filterOptions.copy(showReleaseCandidates = it))
					}
					ChannelSwitch("Pre-releases", facets.preReleases, filterOptions.showPreReleases) {
						onFilterChange(filterOptions.copy(showPreReleases = it))
					}
					ChannelSwitch("Snapshots", facets.snapshots, filterOptions.showSnapshots) {
						onFilterChange(filterOptions.copy(showSnapshots = it))
					}
				}

				FilterGroup("Sort") {
					Div({ classes(ReleaseFiltersStyle.segmented) }) {
						SortOrder.entries.forEach { order ->
							Button({
								classes(ReleaseFiltersStyle.segment)
								if (filterOptions.sortOrder == order) classes(ReleaseFiltersStyle.segmentActive)
								attr("aria-pressed", "${filterOptions.sortOrder == order}")
								onClick { onFilterChange(filterOptions.copy(sortOrder = order)) }
							}) { Text(order.label) }
						}
					}
				}

				if (facets.versionGroups.isNotEmpty()) {
					FilterGroup("Minecraft") {
						facets.versionGroups.forEach { (mainVersion, versions) ->
							Div({ classes(ReleaseFiltersStyle.versionRow) }) {
								versions.forEach { version ->
									val selected = version in filterOptions.selectedMinecraftVersions
									Button({
										classes(ReleaseFiltersStyle.versionChip)
										if (version == mainVersion) classes(ReleaseFiltersStyle.versionChipMain)
										if (selected) classes(ReleaseFiltersStyle.versionChipSelected)
										attr("aria-pressed", "$selected")
										onClick {
											val current = filterOptions.selectedMinecraftVersions
											onFilterChange(filterOptions.copy(selectedMinecraftVersions = if (selected) current - version else current + version))
										}
									}) {
										Text(version)
										Span("${facets.countOf(version, mainVersion)}", ReleaseFiltersStyle.versionChipCount)
									}
								}
							}
						}
					}
				}

				if (filterOptions != ReleaseFilterOptions()) {
					Button({
						classes(ReleaseFiltersStyle.reset)
						onClick { onFilterChange(ReleaseFilterOptions()) }
					}) {
						LucideRotateCcw()
						Text("Reset filters")
					}
				}
			}
		}
	}
}

object ReleaseFiltersStyle : StyleSheet() {
	private const val MONO = "JetBrains Mono"

	/** Sticky next to the timeline on large screens, a regular block above it under lg. */
	val sidebar by style {
		property("align-self", "start")
		property("background", "linear-gradient(180deg, rgba(8, 182, 214, 0.09), rgba(8, 182, 214, 0.02) 45%), #1a2330")
		border(1.px, LineStyle.Solid, Color("rgba(151, 176, 202, 0.24)"))
		boxShadow(0.px, 12.px, 32.px, (-16).px, rgba(0, 0, 0, 0.6))
		borderRadius(1.cssRem)
		boxSizing(BoxSizing.BorderBox)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.1.cssRem)
		property("max-height", "calc(100vh - 7rem)")
		overflowY(Overflow.Auto)
		padding(1.1.cssRem)
		position(Position.Sticky)
		property("scrollbar-width", "thin")
		top(6.cssRem)

		lgMax(self) {
			gap(0.8.cssRem)
			property("max-height", "none")
			overflowY(Overflow.Visible)
			position(Position.Static)
		}
	}

	val search by style {
		alignItems(AlignItems.Center)
		backgroundColor(rgba(255, 255, 255, 0.05))
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(0.7.cssRem)
		display(DisplayStyle.Flex)
		gap(0.55.cssRem)
		padding(0.px, 0.8.cssRem)
		transition(0.2.s, "border-color", "box-shadow")

		"svg" style {
			color(Color("var(--landing-muted)"))
			flexShrink(0)
			fontSize(1.05.cssRem)
		}

		"input" style {
			backgroundColor(Color.transparent)
			border(0.px)
			color(Color("var(--landing-text)"))
			fontSize(0.92.cssRem)
			height(2.5.cssRem)
			minWidth(0.px)
			outlineWidth(0.px)
			width(100.percent)
		}

		"input::placeholder" style {
			color(Color("var(--landing-muted)"))
		}

		self + ":focus-within" style {
			borderColor(Color("rgba(8, 182, 214, 0.6)"))
			boxShadow(0.px, 0.px, 0.px, 3.px, rgba(8, 182, 214, 0.15))
		}
	}

	val panelToggle by style {
		alignItems(AlignItems.Center)
		backgroundColor(rgba(255, 255, 255, 0.05))
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(0.7.cssRem)
		color(Color("var(--landing-text)"))
		cursor(Cursor.Pointer)
		display(DisplayStyle.None)
		fontSize(0.9.cssRem)
		fontWeight(600)
		gap(0.5.cssRem)
		justifyContent(JustifyContent.Center)
		padding(0.6.cssRem, 1.cssRem)

		"svg" style {
			fontSize(1.cssRem)
		}

		lgMax(self) {
			display(DisplayStyle.Flex)
		}
	}

	val panel by style {
		display(DisplayStyle.Grid)
		gridTemplateRows { size(1.fr) }
		transition(0.22.s, "grid-template-rows", "opacity", "visibility")
	}

	val panelCollapsed by style {
		lgMax(self) {
			gridTemplateRows { size(0.fr) }
			opacity(0)
			property("visibility", "hidden")
		}
	}

	val panelContent by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.1.cssRem)
		minHeight(0.px)
		overflow(Overflow.Hidden)

		lgMax(self) {
			display(DisplayStyle.Grid)
			gap(1.5.cssRem)
			gridTemplateColumns("repeat(2, minmax(0, 1fr))")
			paddingTop(0.4.cssRem)
		}

		smMax(self) {
			gridTemplateColumns("minmax(0, 1fr)")
		}
	}

	val group by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.45.cssRem)

		lgMin(self + ":not(:first-child)") {
			borderTop(1.px, LineStyle.Solid, Color("var(--landing-border)"))
			paddingTop(1.1.cssRem)
		}
	}

	val groupLabel by style {
		color(Color("var(--landing-muted)"))
		fontFamily(MONO, "monospace")
		fontSize(0.72.cssRem)
		letterSpacing(1.5.px)
		marginBottom(0.2.cssRem)
		textTransform(TextTransform.Uppercase)
	}

	val switchRow by style {
		alignItems(AlignItems.Center)
		borderRadius(0.5.cssRem)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		fontSize(0.9.cssRem)
		gap(0.5.cssRem)
		padding(0.35.cssRem, 0.4.cssRem)
		transition(0.2.s, "background-color")
		userSelect(UserSelect.None)

		hover(self) style {
			backgroundColor(rgba(8, 182, 214, 0.07))
		}
	}

	val count by style {
		color(Color("var(--landing-muted)"))
		fontFamily(MONO, "monospace")
		fontSize(0.72.cssRem)
		marginRight(autoLength)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val switch by style {
		property("appearance", "none")
		backgroundColor(Color("var(--landing-border)"))
		borderRadius(999.px)
		cursor(Cursor.Pointer)
		flexShrink(0)
		height(1.1.cssRem)
		margin(0.px)
		position(Position.Relative)
		transition(0.2.s, "background-color")
		width(2.cssRem)

		self + before style {
			backgroundColor(Color("var(--landing-text)"))
			borderRadius(50.percent)
			property("content", "''")
			height(0.8.cssRem)
			left(0.15.cssRem)
			position(Position.Absolute)
			top(0.15.cssRem)
			transition(0.2.s, "transform")
			width(0.8.cssRem)
		}

		self + checked style {
			backgroundColor(Color("var(--landing-accent)"))
		}

		(self + checked + before) style {
			transform { translateX(0.9.cssRem) }
		}

		self + focusVisible style {
			outline("2px solid var(--landing-accent-strong)")
			property("outline-offset", "2px")
		}
	}

	val segmented by style {
		backgroundColor(rgba(255, 255, 255, 0.05))
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(999.px)
		display(DisplayStyle.Grid)
		gap(0.25.cssRem)
		gridTemplateColumns("repeat(2, minmax(0, 1fr))")
		padding(0.25.cssRem)
	}

	val segment by style {
		backgroundColor(Color.transparent)
		border(0.px)
		borderRadius(999.px)
		color(Color("var(--landing-muted)"))
		cursor(Cursor.Pointer)
		fontSize(0.85.cssRem)
		padding(0.35.cssRem, 0.8.cssRem)
		transition(0.2.s, "background-color", "color", "scale")

		hover(self) style {
			color(Color("var(--landing-text)"))
		}

		self + active style {
			property("scale", "0.95")
		}
	}

	val segmentActive by style {
		backgroundColor(rgba(8, 182, 214, 0.22))
		color(Color("var(--landing-text)"))
	}

	val versionRow by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.35.cssRem)
	}

	val versionChip by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color.transparent)
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(0.45.cssRem)
		color(Color("var(--landing-muted)"))
		cursor(Cursor.Pointer)
		property("display", "inline-flex")
		fontFamily(MONO, "monospace")
		fontSize(0.76.cssRem)
		gap(0.35.cssRem)
		padding(0.2.cssRem, 0.5.cssRem)
		transition(0.2.s, "background-color", "border-color", "color", "scale")

		hover(self) style {
			backgroundColor(rgba(8, 182, 214, 0.08))
			color(Color("var(--landing-text)"))
		}

		self + active style {
			property("scale", "0.94")
		}
	}

	val versionChipMain by style {
		backgroundColor(rgba(255, 255, 255, 0.05))
		color(Color("var(--landing-text)"))
		fontWeight(600)
	}

	val versionChipSelected by style {
		backgroundColor(rgba(8, 182, 214, 0.2))
		borderColor(Color("rgba(8, 182, 214, 0.7)"))
		color(Color("var(--landing-text)"))

		hover(self) style {
			backgroundColor(rgba(8, 182, 214, 0.28))
		}
	}

	val versionChipCount by style {
		opacity(0.6)
		fontSize(0.68.cssRem)
	}

	val reset by style {
		alignItems(AlignItems.Center)
		property("align-self", "flex-start")
		backgroundColor(Color.transparent)
		border(0.px)
		color(Color("var(--landing-muted)"))
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		fontSize(0.85.cssRem)
		gap(0.4.cssRem)
		padding(0.px)
		transition(0.2.s, "color")

		hover(self) style {
			color(Color("var(--landing-text)"))
		}
	}
}
