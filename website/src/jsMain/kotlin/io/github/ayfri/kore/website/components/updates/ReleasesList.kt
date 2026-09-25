package io.github.ayfri.kore.website.components.updates

import androidx.compose.runtime.*
import com.varabyte.kobweb.browser.storage.BooleanStorageKey
import com.varabyte.kobweb.browser.storage.getItem
import com.varabyte.kobweb.browser.storage.setItem
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.components.icons.lucide.LucideChevronsDownUp
import com.varabyte.kobweb.silk.components.icons.lucide.LucideChevronsUpDown
import com.varabyte.kobweb.silk.components.icons.lucide.LucideExternalLink
import io.github.ayfri.kore.website.components.common.BrandIcon
import io.github.ayfri.kore.website.utils.*
import kotlinx.browser.document
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

/** Changelogs past this length, or with images, start collapsed so the timeline stays scannable. */
private const val COLLAPSIBLE_BODY_LENGTH = 700

private val MinecraftVersionPattern?.channel
	get() = when (this) {
		MinecraftVersionPattern.RELEASE -> "Release" to ReleasesListStyle.channelRelease
		MinecraftVersionPattern.RELEASE_CANDIDATE -> "Release candidate" to ReleasesListStyle.channelCandidate
		MinecraftVersionPattern.PRE_RELEASE -> "Pre-release" to ReleasesListStyle.channelPreRelease
		MinecraftVersionPattern.SNAPSHOT -> "Snapshot" to ReleasesListStyle.channelSnapshot
		null -> null
	}

/** Consecutive releases sharing a `major.minor` Minecraft version, rendered under one timeline heading. */
private class ReleaseGroup(val minecraftVersion: String?, val releases: MutableList<GitHubRelease> = mutableListOf())

@Composable
fun ReleasesList(releases: List<GitHubRelease>) {
	Style(ReleasesListStyle)
	// Injected here rather than in `MarkdownRenderer`: `Style` emits a <style> tag per call, and there is one renderer per card.
	Style(MarkdownRendererStyle)

	val allReleases = remember(releases) { releases.sortedByDescending { it.publishedAt } }
	var filterOptions by remember {
		mutableStateOf(
			ReleaseFilterOptions(
				showPreReleases = localStorage.getItem(ShowPreReleasesStorageKey) ?: false,
				showSnapshots = localStorage.getItem(ShowSnapshotsStorageKey) ?: false,
				showReleaseCandidates = localStorage.getItem(ShowReleaseCandidatesStorageKey) ?: false
			)
		)
	}

	val filteredReleases = remember(allReleases, filterOptions) {
		val matching = allReleases.filter { it.matchesFilters(filterOptions) }
		if (filterOptions.sortOrder == SortOrder.OLDEST_FIRST) matching.asReversed() else matching
	}

	val groups = remember(filteredReleases) {
		buildList<ReleaseGroup> {
			filteredReleases.forEach { release ->
				val last = lastOrNull()?.takeIf { it.minecraftVersion == release.mainMinecraftVersion }
				(last ?: ReleaseGroup(release.mainMinecraftVersion).also { add(it) }).releases += release
			}
		}
	}

	Div({ classes(ReleasesListStyle.layout) }) {
		ReleaseFilters(allReleases, filterOptions) { newOptions ->
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
		}

		Div({ classes(ReleasesListStyle.main) }) {
			Header({ classes(ReleasesListStyle.header) }) {
				Span("Changelog", ReleasesListStyle.eyebrow)
				H1 { Text("Releases") }
				P("Every Kore release with its full notes, pulled from GitHub at build time.")
				P(
					"Changelogs cover what changed between two Minecraft versions, so a release with nothing new in-between has a short or empty entry.",
					ReleasesListStyle.note
				)
				ReleaseStats(allReleases)
			}

			Div({
				id("changelogs")
				classes(ReleasesListStyle.resultBar)
			}) {
				Span("${filteredReleases.size} of ${allReleases.size} releases")
				if (!filterOptions.showSnapshots || !filterOptions.showPreReleases || !filterOptions.showReleaseCandidates) {
					Span("Unstable channels are hidden by default", ReleasesListStyle.resultHint)
				}
			}

			if (groups.isEmpty()) {
				Div({ classes(ReleasesListStyle.emptyState) }) {
					H3 { Text("No releases match your filters") }
					P("Try another search, or enable more channels.")
				}
			}

			groups.forEach { group ->
				// Keyed by its oldest release, which is the same in both sort orders, so flipping the sort moves groups
				// instead of re-mounting every release inside a different group.
				key(group.releases.minOf { it.id }) {
					ReleaseGroupSection(group)
				}
			}
		}
	}
}

@Composable
private fun ReleaseGroupSection(group: ReleaseGroup) {
	Section({ classes(ReleasesListStyle.group) }) {
		H2({ classes(ReleasesListStyle.groupTitle) }) {
			Text(group.minecraftVersion?.let { "Minecraft $it" } ?: "Other releases")
			Span("${group.releases.size}", ReleasesListStyle.groupCount)
		}
		group.releases.forEach { release ->
			key(release.id) { ReleaseEntry(release, isLatest = release === GitHubService.latestRelease) }
		}
	}
}

@Composable
private fun ReleaseEntry(release: GitHubRelease, isLatest: Boolean) {
	val collapsible = remember(release.body) {
		release.body.length > COLLAPSIBLE_BODY_LENGTH || "<img" in release.body || "![" in release.body
	}
	var expanded by remember { mutableStateOf(isLatest) }
	val entryId = "release-${release.id}"

	Article({
		id(entryId)
		classes(ReleasesListStyle.entry)
	}) {
		Div({ classes(ReleasesListStyle.date) }) {
			Span(formatShortDate(release.publishedAt))
			Span(formatRelativeDate(release.publishedAt), ReleasesListStyle.relativeDate)
		}

		Div({
			classes(ReleasesListStyle.card)
			if (isLatest) classes(ReleasesListStyle.cardLatest)
		}) {
			Div({ classes(ReleasesListStyle.cardHead) }) {
				Div({ classes(ReleasesListStyle.titleRow) }) {
					H3 { Text(release.koreVersion ?: release.name) }
					release.minecraftVersion?.let { Span("MC $it", ReleasesListStyle.mcVersion) }
					release.versionKind.channel?.let { (label, style) -> Span(label, ReleasesListStyle.pill, style) }
					if (isLatest) Span("Latest", ReleasesListStyle.pill, ReleasesListStyle.pillLatest)
				}

				Div({ classes(ReleasesListStyle.links) }) {
					A(release.htmlUrl, {
						classes(ReleasesListStyle.externalLink)
						target(ATarget.Blank)
						rel("noopener", "noreferrer")
					}) {
						BrandIcon("github")
						Text(release.tagName)
					}
					buildMinecraftChangelogUrl(release)?.let { changelogUrl ->
						A(changelogUrl, {
							classes(ReleasesListStyle.externalLink)
							target(ATarget.Blank)
							rel("noopener", "noreferrer")
						}) {
							LucideExternalLink()
							Text("Minecraft changelog")
						}
					}
				}
			}

			if (release.body.isBlank()) {
				P("No changelog for this release.", ReleasesListStyle.emptyBody)
			} else {
				Div({
					classes(ReleasesListStyle.body)
					if (collapsible && !expanded) classes(ReleasesListStyle.bodyCollapsed)
				}) {
					MarkdownRenderer(release.body, "release-body-${release.id}")
				}
			}

			if (collapsible) {
				Button({
					classes(ReleasesListStyle.expandButton)
					attr("aria-expanded", "$expanded")
					onClick {
						expanded = !expanded
						// Collapsing a long changelog would leave the reader far below it, so bring its top back into view.
						val entry = document.getElementById(entryId)
						if (!expanded && entry != null && entry.getBoundingClientRect().top < 0) entry.scrollIntoView()
					}
				}) {
					if (expanded) LucideChevronsDownUp() else LucideChevronsUpDown()
					Text(if (expanded) "Collapse" else "Read full changelog")
				}
			}
		}
	}
}

object ReleasesListStyle : StyleSheet() {
	private const val MONO = "JetBrains Mono"
	private const val SANS = "IBM Plex Sans"

	@OptIn(ExperimentalComposeWebApi::class)
	val rise by keyframes {
		from {
			opacity(0)
			transform { translateY(10.px) }
		}
		to {
			opacity(1)
			transform { translateY(0.px) }
		}
	}

	/** Under the lg breakpoint the filters sidebar moves above the timeline. */
	val layout by style {
		boxSizing(BoxSizing.BorderBox)
		display(DisplayStyle.Grid)
		gap(2.5.cssRem)
		gridTemplateColumns("15rem minmax(0, 1fr)")
		marginX(autoLength)
		maxWidth(96.cssRem)
		padding(2.cssRem, 3.vw, 1.cssRem)
		width(100.percent)

		lgMax(self) {
			gap(1.5.cssRem)
			gridTemplateColumns("minmax(0, 1fr)")
		}

		smMax(self) {
			padding(1.5.cssRem, 1.1.cssRem, 1.cssRem)
		}
	}

	val main by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		minWidth(0.px)

		lgMax(self) {
			property("grid-row", "1")
		}
	}

	val header by style {
		animation(rise) {
			duration(0.5.s)
			timingFunction(AnimationTimingFunction.EaseOut)
		}
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.4.cssRem)
		paddingBottom(1.4.cssRem)

		"h1" style {
			fontSize(1.9.cssRem)
			letterSpacing((-0.6).px)
			lineHeight(1.15.number)
			margin(0.px)
		}

		"p" style {
			color(Color("var(--landing-muted)"))
			fontSize(0.95.cssRem)
			lineHeight(1.55.number)
			margin(0.px)
			maxWidth(52.cssRem)
		}
	}

	val eyebrow by style {
		color(Color("var(--landing-accent)"))
		fontFamily(MONO, "monospace")
		fontSize(0.78.cssRem)
		letterSpacing(1.5.px)
		textTransform(TextTransform.Uppercase)
	}

	val note by style {
		fontSize(0.82.cssRem)
		opacity(0.8)
	}

	val resultBar by style {
		alignItems(AlignItems.Baseline)
		borderBottom(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		color(Color("var(--landing-muted)"))
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		fontFamily(MONO, "monospace")
		fontSize(0.8.cssRem)
		gap(0.4.cssRem, 1.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
		paddingBottom(0.8.cssRem)
		property("scroll-margin-top", "6rem")
	}

	val resultHint by style {
		opacity(0.7)
	}

	val group by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		paddingTop(2.2.cssRem)
	}

	val groupTitle by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		fontFamily(SANS, "sans-serif")
		fontSize(1.25.cssRem)
		gap(0.6.cssRem)
		letterSpacing((-0.3).px)
		margin(0.px, 0.px, 1.2.cssRem)
	}

	val groupCount by style {
		backgroundColor(Color("var(--landing-card)"))
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(999.px)
		color(Color("var(--landing-muted)"))
		fontFamily(MONO, "monospace")
		fontSize(0.72.cssRem)
		fontWeight(400)
		padding(0.1.cssRem, 0.5.cssRem)
	}

	/**
	 * A date rail on the left and the card on the right, joined by a timeline line and a dot per release.
	 * `content-visibility` clips paint to the box, so entries are spaced with padding for the line to reach the next one.
	 */
	val entry by style {
		display(DisplayStyle.Grid)
		gap(1.2.cssRem)
		gridTemplateColumns("6.5rem minmax(0, 1fr)")
		paddingBottom(1.2.cssRem)
		position(Position.Relative)
		property("scroll-margin-top", "6rem")
		// Skip layout and paint for the entries that are off-screen, there are hundreds of them.
		property("content-visibility", "auto")
		containIntrinsicSize(ContainIntrinsicSize.Auto(260.px))

		self + before style {
			backgroundColor(Color("var(--landing-border)"))
			bottom(0.px)
			property("content", "''")
			property("left", "calc(7.1rem - 1px)")
			position(Position.Absolute)
			top(0.px)
			width(2.px)
		}

		self + ":last-child::before" style {
			property("bottom", "auto")
			height(2.cssRem)
		}

		mdMax(self) {
			gap(0.5.cssRem)
			gridTemplateColumns("minmax(0, 1fr)")

			self + before style {
				display(DisplayStyle.None)
			}
		}
	}

	val date by style {
		alignItems(AlignItems.FlexEnd)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		fontFamily(MONO, "monospace")
		fontSize(0.82.cssRem)
		gap(0.2.cssRem)
		paddingTop(1.35.cssRem)
		textAlign(TextAlign.Right)

		mdMax(self) {
			alignItems(AlignItems.Baseline)
			flexDirection(FlexDirection.Row)
			gap(0.6.cssRem)
			paddingTop(0.px)
		}
	}

	val relativeDate by style {
		color(Color("var(--landing-muted)"))
		fontSize(0.74.cssRem)
	}

	val card by style {
		backgroundColor(Color("var(--landing-card)"))
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(1.cssRem)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
		minWidth(0.px)
		padding(1.3.cssRem, 1.8.cssRem)
		position(Position.Relative)
		transition(0.25.s, "border-color", "background-color", "box-shadow")
		animation(rise) {
			duration(0.4.s)
			timingFunction(AnimationTimingFunction.EaseOut)
		}

		// The dot sits in the gap between the date rail and the card.
		self + before style {
			backgroundColor(Color("var(--landing-surface)"))
			border(2.px, LineStyle.Solid, Color("var(--landing-muted)"))
			borderRadius(50.percent)
			property("content", "''")
			boxSizing(BoxSizing.BorderBox)
			height(0.7.cssRem)
			left((-0.95).cssRem)
			position(Position.Absolute)
			top(1.6.cssRem)
			transition(0.25.s, "background-color", "border-color", "scale")
			width(0.7.cssRem)
		}

		hover(self) style {
			backgroundColor(Color("color-mix(in srgb, var(--landing-card), #fff 2.5%)"))
			borderColor(Color("rgba(8, 182, 214, 0.45)"))
			boxShadow(0.px, 8.px, 24.px, (-12).px, rgba(0, 0, 0, 0.5))
		}

		self + ":hover::before" style {
			borderColor(Color("var(--landing-accent-strong)"))
			property("scale", "1.25")
		}

		mdMax(self) {
			padding(1.1.cssRem)

			self + before style {
				display(DisplayStyle.None)
			}
		}
	}

	val cardLatest by style {
		borderColor(Color("rgba(8, 182, 214, 0.5)"))
		boxShadow(0.px, 0.px, 0.px, 3.px, rgba(8, 182, 214, 0.08))

		self + before style {
			backgroundColor(Color("var(--landing-accent-strong)"))
			borderColor(Color("var(--landing-accent-strong)"))
			boxShadow(0.px, 0.px, 0.px, 4.px, rgba(8, 182, 214, 0.2))
		}
	}

	val cardHead by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.55.cssRem)
	}

	val titleRow by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.5.cssRem, 0.6.cssRem)

		"h3" style {
			fontFamily(MONO, "monospace")
			fontSize(1.35.cssRem)
			fontWeight(700)
			letterSpacing((-0.5).px)
			margin(0.px)
			marginRight(0.2.cssRem)
		}
	}

	val mcVersion by style {
		color(Color("var(--landing-muted)"))
		fontFamily(MONO, "monospace")
		fontSize(0.85.cssRem)
	}

	val pill by style {
		border(1.px, LineStyle.Solid, Color("currentColor"))
		borderRadius(999.px)
		fontSize(0.7.cssRem)
		fontWeight(600)
		letterSpacing(0.3.px)
		padding(0.1.cssRem, 0.55.cssRem)
	}

	val channelRelease by style {
		backgroundColor(rgba(8, 182, 214, 0.1))
		color(Color("var(--landing-accent-strong)"))
	}

	val channelCandidate by style {
		backgroundColor(rgba(74, 222, 128, 0.1))
		color(rgb(110, 225, 150))
	}

	val channelPreRelease by style {
		backgroundColor(rgba(254, 201, 7, 0.1))
		color(Color("var(--landing-gold)"))
	}

	val channelSnapshot by style {
		backgroundColor(rgba(192, 132, 252, 0.1))
		color(rgb(200, 160, 250))
	}

	val pillLatest by style {
		backgroundColor(Color("var(--landing-accent)"))
		borderColor(Color("var(--landing-accent)"))
		color(Color("var(--landing-surface)"))
	}

	val links by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.3.cssRem, 1.1.cssRem)
	}

	val externalLink by style {
		alignItems(AlignItems.Center)
		color(Color("var(--landing-muted)"))
		property("display", "inline-flex")
		fontSize(0.8.cssRem)
		gap(0.35.cssRem)
		textDecorationLine(TextDecorationLine.None)
		transition(0.2.s, "color")

		"svg" style {
			fontSize(0.85.cssRem)
		}

		hover(self) style {
			color(Color("var(--landing-accent-strong)"))
		}
	}

	/** `calc-size()` animates the expansion to `auto`, the `max-height` fallback for older browsers makes collapsing instant. */
	val body by style {
		borderTop(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		property("height", "calc-size(auto, size)")
		overflow(Overflow.Hidden)
		paddingTop(0.4.cssRem)
		transition(0.4.s, "height")
	}

	val bodyCollapsed by style {
		property("height", "calc-size(auto, min(size, 15rem))")
		maxHeight(15.cssRem)
		property("mask-image", "linear-gradient(to bottom, #000 55%, transparent)")
	}

	val emptyBody by style {
		color(Color("var(--landing-muted)"))
		fontSize(0.9.cssRem)
		fontStyle(FontStyle.Italic)
		margin(0.px)
	}

	val expandButton by style {
		alignItems(AlignItems.Center)
		property("align-self", "flex-start")
		backgroundColor(Color.transparent)
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(999.px)
		color(Color("var(--landing-text)"))
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		fontSize(0.82.cssRem)
		gap(0.4.cssRem)
		padding(0.35.cssRem, 0.9.cssRem)
		transition(0.2.s, "background-color", "border-color")

		"svg" style {
			fontSize(0.9.cssRem)
		}

		hover(self) style {
			backgroundColor(rgba(8, 182, 214, 0.1))
			borderColor(Color("rgba(8, 182, 214, 0.6)"))
		}
	}

	val emptyState by style {
		alignItems(AlignItems.Center)
		border(1.px, LineStyle.Dashed, Color("var(--landing-border)"))
		borderRadius(1.cssRem)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.5.cssRem)
		marginTop(2.cssRem)
		padding(3.cssRem, 1.5.cssRem)
		textAlign(TextAlign.Center)

		"h3" style {
			fontSize(1.2.cssRem)
			margin(0.px)
		}

		"p" style {
			color(Color("var(--landing-muted)"))
			margin(0.px)
		}
	}
}
