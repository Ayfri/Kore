package io.github.ayfri.kore.website.components.updates

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.AlignSelf
import com.varabyte.kobweb.compose.css.functions.calc
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.kobweb.silk.components.icons.lucide.LucideFunnel
import com.varabyte.kobweb.silk.components.icons.lucide.LucideRotateCcw
import com.varabyte.kobweb.silk.components.icons.lucide.LucideSearch
import io.github.ayfri.kore.website.LandingVars
import io.github.ayfri.kore.website.components.common.Segmented
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
					Segmented(
						SortOrder.entries,
						filterOptions.sortOrder,
						{ onFilterChange(filterOptions.copy(sortOrder = it)) },
						{ it.label },
						ReleaseFiltersStyle.sortBar,
					)
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
	/** Sticky next to the timeline on large screens, a regular block above it under lg. */
	val sidebar by style {
		alignSelf(AlignSelf.Start)
		background(
			Background.list(
				Color("#1a2330"),
				Background.of(
					BackgroundImage.of(
						linearGradient(180.deg) {
							add(LandingVars.Accent.value().alpha(0.07))
							add(Color.transparent, 45.percent)
						}
					)
				),
			)
		)
		border(1.px, LineStyle.Solid, Color("rgba(151, 176, 202, 0.24)"))
		boxShadow(0.px, 12.px, 32.px, (-16).px, rgba(0, 0, 0, 0.6))
		borderRadius(1.cssRem)
		boxSizing(BoxSizing.BorderBox)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.1.cssRem)
		maxHeight(calc { 100.vh - 7.cssRem })
		overflowY(Overflow.Auto)
		padding(1.1.cssRem)
		position(Position.Sticky)
		scrollbarWidth(ScrollbarWidth.Thin)
		top(6.cssRem)

		lgMax(self) {
			gap(0.8.cssRem)
			maxHeight(MaxHeight.None)
			overflowY(Overflow.Visible)
			position(Position.Static)
		}
	}

	val search by style {
		alignItems(AlignItems.Center)
		backgroundColor(LandingVars.Text.value().alpha(0.06))
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(0.7.cssRem)
		display(DisplayStyle.Flex)
		gap(0.55.cssRem)
		padding(0.px, 0.8.cssRem)
		transition(0.2.s, "border-color", "box-shadow")

		"svg" style {
			color(LandingVars.Muted.value())
			flexShrink(0)
			fontSize(1.05.cssRem)
		}

		"input" style {
			backgroundColor(Color.transparent)
			border(0.px)
			color(LandingVars.Text.value())
			fontSize(0.92.cssRem)
			height(2.5.cssRem)
			minWidth(0.px)
			outlineWidth(0.px)
			width(100.percent)
		}

		"input::placeholder" style {
			color(LandingVars.Muted.value())
		}

		self + ":focus-within" style {
			borderColor(LandingVars.Accent.value().alpha(0.5))
			boxShadow(0.px, 0.px, 0.px, 3.px, LandingVars.Accent.value().alpha(0.12))
		}
	}

	val panelToggle by style {
		alignItems(AlignItems.Center)
		backgroundColor(LandingVars.Text.value().alpha(0.06))
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(0.7.cssRem)
		color(LandingVars.Text.value())
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
			visibility(Visibility.Hidden)
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
			borderTop(1.px, LineStyle.Solid, LandingVars.Border.value())
			paddingTop(1.1.cssRem)
		}
	}

	val groupLabel by style {
		color(LandingVars.Muted.value())
		marginBottom(0.2.cssRem)
		monoLabel(0.72.cssRem)
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
			backgroundColor(LandingVars.Accent.value().alpha(0.07))
		}
	}

	val count by style {
		color(LandingVars.Muted.value())
		fontSize(0.72.cssRem)
		marginRight(autoLength)
		monoFont()
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val switch by style {
		appearance(Appearance.None)
		backgroundColor(LandingVars.Border.value())
		borderRadius(999.px)
		cursor(Cursor.Pointer)
		flexShrink(0)
		height(1.1.cssRem)
		margin(0.px)
		position(Position.Relative)
		transition(0.2.s, "background-color")
		width(2.cssRem)

		self + before style {
			backgroundColor(LandingVars.Text.value())
			borderRadius(50.percent)
			content("")
			height(0.8.cssRem)
			left(0.15.cssRem)
			position(Position.Absolute)
			top(0.15.cssRem)
			transition(0.2.s, "transform")
			width(0.8.cssRem)
		}

		self + checked style {
			backgroundColor(LandingVars.Accent.value())
		}

		(self + checked + before) style {
			transform { translateX(0.9.cssRem) }
		}

		self + focusVisible style {
			outline("2px solid ${LandingVars.AccentStrong.value()}")
			outlineOffset(2.px)
		}
	}

	/** Both sort orders share the sidebar width evenly. */
	val sortBar by style {
		backgroundColor(LandingVars.Text.value().alpha(0.06))
		display(DisplayStyle.Grid)
		gridTemplateColumns("repeat(2, minmax(0, 1fr))")
		width(100.percent)
	}

	val versionRow by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.35.cssRem)
	}

	val versionChip by style {
		alignItems(AlignItems.Center)
		backgroundColor(Color.transparent)
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(0.45.cssRem)
		color(LandingVars.Muted.value())
		cursor(Cursor.Pointer)
		display(DisplayStyle.LegacyInlineFlex)
		fontSize(0.76.cssRem)
		gap(0.35.cssRem)
		monoFont()
		padding(0.2.cssRem, 0.5.cssRem)
		transition(0.2.s, "background-color", "border-color", "color", "scale")

		hover(self) style {
			backgroundColor(LandingVars.Accent.value().alpha(0.07))
			color(LandingVars.Text.value())
		}

		self + active style {
			scale(0.94)
		}
	}

	val versionChipMain by style {
		backgroundColor(LandingVars.Text.value().alpha(0.06))
		color(LandingVars.Text.value())
		fontWeight(600)
	}

	val versionChipSelected by style {
		backgroundColor(LandingVars.Accent.value().alpha(0.2))
		borderColor(LandingVars.Accent.value().alpha(0.5))
		color(LandingVars.Text.value())

		hover(self) style {
			backgroundColor(LandingVars.Accent.value().alpha(0.35))
		}
	}

	val versionChipCount by style {
		opacity(0.6)
		fontSize(0.68.cssRem)
	}

	val reset by style {
		alignItems(AlignItems.Center)
		alignSelf(AlignSelf.FlexStart)
		backgroundColor(Color.transparent)
		border(0.px)
		color(LandingVars.Muted.value())
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		fontSize(0.85.cssRem)
		gap(0.4.cssRem)
		padding(0.px)
		transition(0.2.s, "color")

		hover(self) style {
			color(LandingVars.Text.value())
		}
	}
}
