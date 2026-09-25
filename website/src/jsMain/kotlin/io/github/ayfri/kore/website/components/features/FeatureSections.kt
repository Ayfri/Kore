package io.github.ayfri.kore.website.components.features

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.silk.components.icons.lucide.LucideCircleX
import com.varabyte.kobweb.silk.components.icons.lucide.LucideCode
import com.varabyte.kobweb.silk.components.icons.lucide.LucideTriangleAlert
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.*
import io.github.ayfri.kore.website.utils.*
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.events.Event
import org.jetbrains.compose.web.dom.A as DomA

private class NavEntry(val id: String, val title: String, val count: Int?, val icon: @Composable () -> Unit)

private val navEntries = buildList {
	add(NavEntry("output", "Generated output", null) { LucideCode() })
	featureCategories.forEach { add(NavEntry(it.id, it.title, it.items.size + it.chips.size, it.icon)) }
	add(NavEntry("limits", "Limits", null) { LucideTriangleAlert() })
}

/** Distance from the viewport top under which a section counts as the one being read. */
private const val ACTIVE_SECTION_OFFSET_PX = 160

/** Sticky table of contents highlighting the section being read, a horizontal strip under the xl breakpoint. */
@Composable
fun FeaturesNav() {
	var active by remember { mutableStateOf(navEntries.first().id) }

	window.onEvents("scroll" to { _: Event ->
		active = navEntries.lastOrNull {
			(document.getElementById(it.id)?.getBoundingClientRect()?.top ?: Double.MAX_VALUE) <= ACTIVE_SECTION_OFFSET_PX
		}?.id ?: navEntries.first().id
	})

	Nav({ classes(FeatureSectionsStyle.toc) }) {
		Span("On this page", FeatureSectionsStyle.tocTitle)
		navEntries.forEach { entry ->
			DomA("#${entry.id}", {
				classes(FeatureSectionsStyle.tocLink)
				if (entry.id == active) classes(FeatureSectionsStyle.tocLinkActive)
			}) {
				entry.icon()
				Span(entry.title)
				entry.count?.let { Span(it.toString(), FeatureSectionsStyle.tocCount) }
			}
		}
	}
}

@Composable
fun FeaturesHeader() {
	Header({ classes(FeatureSectionsStyle.header) }) {
		Span(
			"Kore ${AppGlobals.getValue("projectVersion")} · Minecraft ${AppGlobals.getValue("minecraftVersion")}",
			FeatureSectionsStyle.version
		)
		H1 { Text("Features") }
		P("Everything Kore covers, grouped by area. Each entry links to its guide, and hovering one previews it in game.")
	}
}

@Composable
fun ShowcaseSection() {
	Section({
		id("output")
		classes(FeatureSectionsStyle.block)
	}) {
		Heading("Generated output", "The Kotlin you write next to the exact files Kore generates from it. Nothing else ships.") { LucideCode() }

		val tabs = showcases.map { showcase ->
			Tab(showcase.name) {
				Div({ classes(FeatureSectionsStyle.showcaseGrid) }) {
					Div({ classes(FeatureSectionsStyle.codePanel) }) {
						Span("You write", FeatureSectionsStyle.codePanelLabel)
						CodeBlock(showcase.kotlin, "kotlin")
					}
					Div({ classes(FeatureSectionsStyle.codePanel) }) {
						Span("Kore generates", FeatureSectionsStyle.codePanelLabel)
						showcase.outputs.forEach { file ->
							Span(file.path, FeatureSectionsStyle.filePath)
							CodeBlock(file.code, file.language)
						}
					}
				}
			}
		}

		Tabs(tabs)
	}
}

/** Items and chips sit next to the category's scenes, hovering one switches the stage to its scene. */
@Composable
fun CategorySection(category: FeatureCategory) {
	val scenes = category.scenes
	var active by remember { mutableStateOf(0) }
	val stageId = "stage-${category.id}"
	if (scenes.isNotEmpty()) highlightCodeIn(stageId, active)

	Section({
		id(category.id)
		classes(FeatureSectionsStyle.block)
	}) {
		Heading(category.title, category.tagline, category.icon)

		Div({
			classes(FeatureSectionsStyle.body)
			if (scenes.isEmpty()) classes(FeatureSectionsStyle.bodyWide)
		}) {
			Div({ classes(FeatureSectionsStyle.details) }) {
				if (category.items.isNotEmpty()) {
					Div({ classes(FeatureSectionsStyle.linkList) }) {
						category.items.forEach { item ->
							DomA(item.href, {
								classes(FeatureSectionsStyle.linkRow)
								if (item.href.startsWith("http")) target(ATarget.Blank)
								item.scene?.let { scene ->
									classes(FeatureSectionsStyle.previewable)
									if (scenes.size > 1 && scene == active) classes(FeatureSectionsStyle.previewing)
									onMouseEnter { active = scene }
									onFocusIn { active = scene }
								}
							}) {
								Span({ classes(FeatureSectionsStyle.linkName) }) {
									Text(item.name)
									item.logos.forEach { BrandIcon(it) }
								}
								Span(item.description, FeatureSectionsStyle.linkDescription)
							}
						}
					}
				}

				if (category.chips.isNotEmpty()) {
					Div({ classes(FeatureSectionsStyle.chipGrid) }) {
						category.chips.forEach { chip ->
							chip.href?.let { href ->
								DomA(href, {
									classes(FeatureSectionsStyle.chip, FeatureSectionsStyle.chipLinked)
									chip.scene?.let { scene ->
										classes(FeatureSectionsStyle.chipPreviewable)
										if (scene == active) classes(FeatureSectionsStyle.previewing)
										onMouseEnter { active = scene }
										onFocusIn { active = scene }
									}
								}) { Text(chip.name) }
							} ?: Span(chip.name, FeatureSectionsStyle.chip)
						}
					}
				}
			}

			if (scenes.isEmpty()) return@Div

			Div({ classes(FeatureSectionsStyle.visual) }) {
				Div({
					id(stageId)
					classes(FeatureSectionsStyle.stage)
				}) {
					if (scenes.size > 1) {
						Div({ classes(FeatureSectionsStyle.sceneTabs) }) {
							scenes.forEachIndexed { index, scene ->
								Button({
									classes(FeatureSectionsStyle.sceneTab)
									if (index == active) classes(FeatureSectionsStyle.sceneTabActive)
									onClick { active = index }
								}) { Text(scene.name) }
							}
						}
					}
					key(active) {
						Div({ classes(FeatureSectionsStyle.sceneBody) }) {
							scenes[active].content { active = it }
						}
					}
				}
			}
		}
	}
}

@Composable
fun LimitsSection() {
	Section({
		id("limits")
		classes(FeatureSectionsStyle.block)
	}) {
		Heading("Limits", "What Kore does not do, so you know before picking a tool.") { LucideTriangleAlert() }

		Div({ classes(FeatureSectionsStyle.limits) }) {
			Limit("Resource packs", "Kore generates datapacks only. Textures, models and sounds still live in a separate resource pack.")
			Limit("Runtime speedups", "Output is the same vanilla commands you would write by hand, so it runs exactly as fast, no faster.")
			Limit("Zero-setup editing", "You need a JDK and Gradle, and a build step sits between editing and /reload.")
		}

		P({ classes(FeatureSectionsStyle.footnote) }) {
			Text("Rough edges are tracked in ")
			A("/docs/advanced/known-issues", "Known Issues")
			Text(", and ")
			A("/docs/guides/why-kore", "Why Kore")
			Text(" covers when another tool fits better.")
		}
	}
}

@Composable
fun FeaturesCta() {
	Aside({ classes(FeatureSectionsStyle.cta) }) {
		Div {
			H2 { Text("Try it on your own pack") }
			P("The getting started guide takes you from an empty folder to a pack running in your world.")
		}
		Div({ classes(FeatureSectionsStyle.ctaActions) }) {
			LinkButton("Get started", "/docs/getting-started", color = ButtonColor.PRIMARY)
			LinkButton("Migrate an existing pack", "/docs/guides/from-datapacks-to-kore", variant = ButtonVariant.OUTLINE)
		}
	}
}

@Composable
private fun Heading(title: String, tagline: String, icon: @Composable () -> Unit) {
	Div({ classes(FeatureSectionsStyle.heading) }) {
		H2 {
			icon()
			Text(title)
		}
		P(tagline)
	}
}

@Composable
private fun Limit(title: String, description: String) {
	Div({ classes(FeatureSectionsStyle.limit) }) {
		LucideCircleX()
		H3 { Text(title) }
		P(description)
	}
}

object FeatureSectionsStyle : StyleSheet() {
	private const val MONO = "JetBrains Mono"
	private const val SANS = "IBM Plex Sans"

	val heroTitleAccent by style {
		textGradient(GlobalStyle.logoRightColor, GlobalStyle.logoLeftColor)
	}

	val stats by style {
		backgroundColor(Color("var(--landing-border)"))
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(1.2.cssRem)
		display(DisplayStyle.Grid)
		gap(1.px)
		gridTemplateColumns("repeat(4, minmax(0, 1fr))")
		marginTop(0.8.cssRem)
		maxWidth(52.cssRem)
		overflow(Overflow.Hidden)
		width(100.percent)

		mdMax(self) {
			gridTemplateColumns("repeat(2, minmax(0, 1fr))")
		}
	}

	val stat by style {
		backgroundColor(Color("var(--landing-card)"))
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.2.cssRem)
		justifyContent(JustifyContent.Center)
		padding(1.1.cssRem, 0.8.cssRem)
	}

	val statValue by style {
		color(Color("var(--landing-accent-strong)"))
		fontFamily(MONO, "monospace")
		fontSize(1.9.cssRem)
		fontWeight(700)
		lineHeight(1.2.number)
	}

	val statLabel by style {
		color(Color("var(--landing-muted)"))
		fontSize(0.88.cssRem)
		whiteSpace(WhiteSpace.NoWrap)
	}

	val section by style {
		boxSizing(BoxSizing.BorderBox)
		marginX(auto)
		maxWidth(80.cssRem)
		padding(4.5.cssRem, 5.vw)
		width(100.percent)

		smMax(self) {
			padding(3.cssRem, 1.1.cssRem)
		}
	}

	/** Under the xl breakpoint the table of contents turns into a strip above the content instead of a sidebar. */
	val layout by style {
		boxSizing(BoxSizing.BorderBox)
		display(DisplayStyle.Grid)
		gap(3.5.cssRem)
		gridTemplateColumns("12.5rem minmax(0, 1fr)")
		marginX(auto)
		maxWidth(90.cssRem)
		padding(3.cssRem, 5.vw, 1.cssRem)
		width(100.percent)

		xlMax(self) {
			gap(0.px)
			gridTemplateColumns("minmax(0, 1fr)")
		}

		smMax(self) {
			padding(1.5.cssRem, 1.1.cssRem, 1.cssRem)
		}
	}

	val toc by style {
		property("align-self", "start")
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(2.px)
		property("max-height", "calc(100vh - 8rem)")
		overflowY(Overflow.Auto)
		position(Position.Sticky)
		top(6.cssRem)

		xlMax(self) {
			backgroundColor(Color("var(--landing-surface)"))
			borderBottom(1.px, LineStyle.Solid, Color("var(--landing-border)"))
			flexDirection(FlexDirection.Row)
			gap(0.4.cssRem)
			marginX((-5).vw)
			overflowX(Overflow.Auto)
			padding(0.5.cssRem, 5.vw)
			property("scrollbar-width", "none")
			top(4.5.cssRem)
			zIndex(2)
		}

		smMax(self) {
			marginX((-1.1).cssRem)
			padding(0.5.cssRem, 1.1.cssRem)
		}
	}

	val tocTitle by style {
		color(Color("var(--landing-muted)"))
		fontFamily(MONO, "monospace")
		fontSize(0.72.cssRem)
		letterSpacing(1.5.px)
		marginBottom(0.6.cssRem)
		paddingLeft(0.7.cssRem)
		textTransform(TextTransform.Uppercase)

		xlMax(self) {
			display(DisplayStyle.None)
		}
	}

	val tocLink by style {
		alignItems(AlignItems.Center)
		borderRadius(0.5.cssRem)
		color(Color("var(--landing-muted)"))
		display(DisplayStyle.Flex)
		flexShrink(0)
		fontSize(0.88.cssRem)
		gap(0.55.cssRem)
		padding(0.4.cssRem, 0.7.cssRem)
		textDecorationLine(TextDecorationLine.None)
		transition(0.2.s, "color", "background-color")
		whiteSpace(WhiteSpace.NoWrap)

		"svg" style {
			flexShrink(0)
			fontSize(0.95.cssRem)
		}

		hover(self) style {
			backgroundColor(rgba(8, 182, 214, 0.07))
			color(Color("var(--landing-text)"))
		}
	}

	val tocLinkActive by style {
		backgroundColor(rgba(8, 182, 214, 0.14))
		color(Color("var(--landing-text)"))

		"svg" style {
			color(Color("var(--landing-accent-strong)"))
		}
	}

	val tocCount by style {
		color(Color("var(--landing-muted)"))
		fontFamily(MONO, "monospace")
		fontSize(0.72.cssRem)
		marginLeft(autoLength)
	}

	val header by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.6.cssRem)
		paddingBottom(1.cssRem)

		"h1" style {
			fontSize(2.6.cssRem)
			letterSpacing((-1).px)
			lineHeight(1.1.number)
			margin(0.px)
		}

		"p" style {
			color(Color("var(--landing-muted)"))
			fontSize(1.05.cssRem)
			margin(0.px)
			maxWidth(44.cssRem)
		}

		xlMax(self) {
			paddingTop(2.cssRem)
		}
	}

	val version by style {
		color(Color("var(--landing-accent)"))
		fontFamily(MONO, "monospace")
		fontSize(0.78.cssRem)
	}

	val block by style {
		borderTop(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		padding(2.8.cssRem, 0.px)
		// Clears the sticky header, and the table of contents strip under xl, when jumping to an anchor.
		property("scroll-margin-top", "5rem")

		xlMax(self) {
			property("scroll-margin-top", "8rem")
		}
	}

	val heading by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.5.cssRem)
		marginBottom(1.8.cssRem)

		"h2" style {
			alignItems(AlignItems.Center)
			display(DisplayStyle.Flex)
			fontFamily(SANS, "sans-serif")
			fontSize(1.7.cssRem)
			gap(0.65.cssRem)
			letterSpacing((-0.4).px)
			margin(0.px)
		}

		"h2 svg" style {
			color(Color("var(--landing-accent)"))
			flexShrink(0)
			fontSize(1.3.cssRem)
		}

		"p" style {
			color(Color("var(--landing-muted)"))
			fontSize(1.02.cssRem)
			margin(0.px)
			maxWidth(46.cssRem)
		}

		smMax(self) {
			"h2" style {
				fontSize(1.45.cssRem)
			}
		}
	}

	val showcaseGrid by style {
		alignItems(AlignItems.Stretch)
		backgroundColor(Color("var(--landing-border)"))
		display(DisplayStyle.Grid)
		gap(1.px)
		gridTemplateColumns("repeat(2, minmax(0, 1fr))")

		lgMax(self) {
			gridTemplateColumns("minmax(0, 1fr)")
		}
	}

	val codePanel by style {
		backgroundColor(Color("var(--landing-surface-2)"))
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.5.cssRem)
		minWidth(0.px)
		padding(1.cssRem)

		// Prism wraps each `pre` in a toolbar div; stretching the last one keeps both panels the same height.
		"> :last-child" style {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			flexGrow(1)
		}

		"pre" style {
			borderRadius(0.6.cssRem)
			flexGrow(1)
			fontSize(0.85.cssRem)
			margin(0.px)
			maxWidth(100.percent)
			overflowX(Overflow.Auto)
		}

		smMax(self) {
			padding(0.7.cssRem)

			"pre" style {
				fontSize(0.72.cssRem)
			}
		}
	}

	val codePanelLabel by style {
		color(Color("var(--landing-accent)"))
		fontFamily(MONO, "monospace")
		fontSize(0.75.cssRem)
		letterSpacing(1.5.px)
		textTransform(TextTransform.Uppercase)
	}

	val filePath by style {
		color(Color("var(--landing-muted)"))
		fontFamily(MONO, "monospace")
		fontSize(0.78.cssRem)
		overflowWrap(OverflowWrap.Anywhere)
	}

	// On narrow screens the visual moves above the details, so it isn't buried under a long list.
	val body by style {
		display(DisplayStyle.Grid)
		gap(3.cssRem)
		gridTemplateAreas("details visual")
		gridTemplateColumns("minmax(0, 1fr) minmax(0, 1.1fr)")

		lgMax(self) {
			gap(1.8.cssRem)
			gridTemplateAreas("visual", "details")
			gridTemplateColumns("minmax(0, 1fr)")
		}
	}

	val linkList by style {
		borderTop(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
	}

	/** Categories without scenes spread their items over two columns instead of leaving the visual column empty. */
	val bodyWide by style {
		gridTemplateAreas("details")
		gridTemplateColumns("minmax(0, 1fr)")

		mdMin(desc(self, className(linkList))) {
			display(DisplayStyle.Grid)
			gridTemplateColumns("repeat(2, minmax(0, 1fr))")
			property("column-gap", "2.5rem")
		}
	}

	val details by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
		gridArea("details")
		minWidth(0.px)
	}

	val visual by style {
		gridArea("visual")
		minWidth(0.px)

		lgMin(self) {
			property("align-self", "start")
			position(Position.Sticky)
			top(8.cssRem)
		}
	}

	val linkRow by style {
		borderBottom(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		color(Color("var(--landing-text)"))
		display(DisplayStyle.Grid)
		gap(1.cssRem)
		gridTemplateColumns("11rem minmax(0, 1fr)")
		padding(0.65.cssRem, 0.4.cssRem)
		textDecorationLine(TextDecorationLine.None)
		transition(0.2.s, "background-color", "padding")

		hover(self) style {
			backgroundColor(rgba(8, 182, 214, 0.07))
			color(Color("var(--landing-text)"))
			paddingLeft(0.8.cssRem)
		}

		smMax(self) {
			gap(0.2.cssRem)
			gridTemplateColumns("minmax(0, 1fr)")
		}
	}

	val linkName by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		fontSize(0.95.cssRem)
		fontWeight(600)
		gap(0.45.cssRem)
	}

	/** Rows and chips that preview a scene get an accent bar, so it's clear hovering them changes the visual. */
	val previewable by style {
		borderLeft(2.px, LineStyle.Solid, Color.transparent)
	}

	val previewing by style {
		backgroundColor(rgba(8, 182, 214, 0.08))
		property("border-left-color", "var(--landing-accent-strong)")
		borderColor(Color("rgba(8, 182, 214, 0.6)"))
	}

	val chipPreviewable by style {
		borderStyle(LineStyle.Dashed)
	}

	val stage by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.8.cssRem)
	}

	val sceneTabs by style {
		backgroundColor(Color("var(--landing-card)"))
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(999.px)
		display(DisplayStyle.Flex)
		gap(0.25.cssRem)
		overflowX(Overflow.Auto)
		padding(0.25.cssRem)
		property("align-self", "flex-start")
		property("scrollbar-width", "none")
		maxWidth(100.percent)
	}

	val sceneTab by style {
		backgroundColor(Color.transparent)
		border(0.px)
		borderRadius(999.px)
		color(Color("var(--landing-muted)"))
		cursor(Cursor.Pointer)
		flexShrink(0)
		fontSize(0.82.cssRem)
		padding(0.35.cssRem, 0.85.cssRem)
		transition(0.2.s, "background-color", "color")

		hover(self) style {
			color(Color("var(--landing-text)"))
		}
	}

	val sceneTabActive by style {
		backgroundColor(rgba(8, 182, 214, 0.22))
		color(Color("var(--landing-text)"))
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val sceneIn by keyframes {
		from {
			opacity(0)
			transform { translateY(8.px) }
		}
		to {
			opacity(1)
			transform { translateY(0.px) }
		}
	}

	val sceneBody by style {
		animation(sceneIn) {
			duration(0.35.s)
			timingFunction(AnimationTimingFunction.EaseOut)
		}
	}

	val linkDescription by style {
		color(Color("var(--landing-muted)"))
		fontSize(0.9.cssRem)
		lineHeight(1.5.number)
	}

	val chipGrid by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.5.cssRem)
	}

	val chip by style {
		backgroundColor(Color("var(--landing-card)"))
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(0.6.cssRem)
		color(Color("var(--landing-text)"))
		fontSize(0.88.cssRem)
		padding(0.4.cssRem, 0.75.cssRem)
		textDecorationLine(TextDecorationLine.None)
	}

	val chipLinked by style {
		transition(0.2.s, "border-color", "background-color")

		hover(self) style {
			backgroundColor(rgba(8, 182, 214, 0.12))
			borderColor(Color("rgba(8, 182, 214, 0.6)"))
			color(Color("var(--landing-text)"))
		}
	}

	val limits by style {
		display(DisplayStyle.Grid)
		gap(1.cssRem)
		gridTemplateColumns("repeat(3, minmax(0, 1fr))")

		mdMax(self) {
			gridTemplateColumns("minmax(0, 1fr)")
		}
	}

	val limit by style {
		border(1.px, LineStyle.Dashed, Color("rgba(254, 201, 7, 0.3)"))
		borderRadius(1.cssRem)
		padding(1.3.cssRem)

		"svg" style {
			color(Color("var(--landing-gold)"))
			fontSize(1.4.cssRem)
		}

		"h3" style {
			fontFamily(SANS, "sans-serif")
			fontSize(1.1.cssRem)
			fontWeight(600)
			margin(0.6.cssRem, 0.px, 0.35.cssRem)
		}

		"p" style {
			color(Color("var(--landing-muted)"))
			fontSize(0.93.cssRem)
			lineHeight(1.55.number)
			margin(0.px)
		}
	}

	val footnote by style {
		color(Color("var(--landing-muted)"))
		fontSize(0.9.cssRem)
		marginTop(1.5.cssRem)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val cta by style {
		alignItems(AlignItems.Center)
		backgroundImage(
			linearGradient(120.deg) {
				add(rgba(8, 182, 214, 0.1), 0.percent)
				add(Color("var(--landing-card)"), 60.percent)
			}
		)
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(1.2.cssRem)
		display(DisplayStyle.Flex)
		gap(1.5.cssRem)
		justifyContent(JustifyContent.SpaceBetween)
		margin(0.5.cssRem, 0.px, 3.cssRem)
		padding(1.6.cssRem, 1.8.cssRem)

		"h2" style {
			fontSize(1.35.cssRem)
			margin(0.px)
		}

		"p" style {
			color(Color("var(--landing-muted)"))
			margin(0.3.cssRem, 0.px, 0.px)
		}

		mdMax(self) {
			alignItems(AlignItems.FlexStart)
			flexDirection(FlexDirection.Column)
		}
	}

	val ctaActions by style {
		display(DisplayStyle.Flex)
		flexShrink(0)
		flexWrap(FlexWrap.Wrap)
		gap(0.8.cssRem)
	}
}
