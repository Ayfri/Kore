package io.github.ayfri.kore.website.components.features

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.AlignSelf
import com.varabyte.kobweb.browser.dom.observers.IntersectionObserver
import com.varabyte.kobweb.compose.css.functions.calc
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.silk.components.icons.lucide.LucideArrowRight
import com.varabyte.kobweb.silk.components.icons.lucide.LucideCircleX
import com.varabyte.kobweb.silk.components.icons.lucide.LucideCode
import com.varabyte.kobweb.silk.components.icons.lucide.LucideTriangleAlert
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.LandingVars
import io.github.ayfri.kore.website.components.common.*
import io.github.ayfri.kore.website.components.index.CtaActions
import io.github.ayfri.kore.website.utils.*
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.asList
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

/**
 * Sticky table of contents highlighting the section being read, a horizontal strip under the xl breakpoint.
 * The observer only fires when a block of the page enters or leaves the top [ACTIVE_SECTION_OFFSET_PX] of the viewport,
 * so section positions are read on those crossings instead of on every scroll event.
 */
@Composable
fun FeaturesNav() {
	var active by remember { mutableStateOf(navEntries.first().id) }
	var viewportHeight by remember { mutableStateOf(window.innerHeight) }

	window.onEvents("resize" to { _: Event -> viewportHeight = window.innerHeight })

	DisposableEffect(viewportHeight) {
		val observer = IntersectionObserver(IntersectionObserver.Options(rootMargin = "0px 0px ${ACTIVE_SECTION_OFFSET_PX - viewportHeight}px 0px")) {
			active = navEntries.lastOrNull {
				(document.getElementById(it.id)?.getBoundingClientRect()?.top ?: Double.MAX_VALUE) <= ACTIVE_SECTION_OFFSET_PX
			}?.id ?: navEntries.first().id
		}
		document.getElementById(navEntries.first().id)?.parentElement?.children?.asList()?.forEach(observer::observe)
		onDispose { observer.disconnect() }
	}

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
		DomA("/updates", { classes(FeatureSectionsStyle.version) }) {
			Text("Kore ${AppGlobals.getValue("projectVersion")} · Minecraft ${AppGlobals.getValue("minecraftVersion")}")
		}
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
								externalTarget(item.href)
								item.scene?.let { scene ->
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
					if (scenes.size > 1) Segmented(scenes.indices.toList(), active, { active = it }, { scenes[it].name })
					key(active) {
						Div({ classes(FeatureSectionsStyle.sceneBody) }) {
							scenes[active].content { active = it }
						}
					}
					scenes[active].example?.let { slug ->
						DomA("/playground#example=$slug", { classes(FeatureSectionsStyle.sceneLink) }) {
							Text("Try it in the playground")
							LucideArrowRight()
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
			P("The getting started guide takes you from an empty folder to a pack running in your world. The playground lets you try Kore in your browser before installing anything.")
		}
		Div({ classes(FeatureSectionsStyle.ctaActions) }) { CtaActions(playground = true) }
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
		alignSelf(AlignSelf.Start)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(2.px)
		maxHeight(calc { 100.vh - 8.cssRem })
		overflowY(Overflow.Auto)
		position(Position.Sticky)
		top(6.cssRem)

		xlMax(self) {
			backgroundColor(LandingVars.Surface.value())
			borderBottom(1.px, LineStyle.Solid, LandingVars.Border.value())
			flexDirection(FlexDirection.Row)
			gap(0.4.cssRem)
			marginX((-5).vw)
			overflowX(Overflow.Auto)
			padding(0.5.cssRem, 5.vw)
			scrollbarWidth(ScrollbarWidth.None)
			top(4.5.cssRem)
			zIndex(2)
		}

		smMax(self) {
			marginX((-1.1).cssRem)
			padding(0.5.cssRem, 1.1.cssRem)
		}
	}

	val tocTitle by style {
		color(LandingVars.Muted.value())
		marginBottom(0.6.cssRem)
		monoLabel(0.72.cssRem)
		paddingLeft(0.7.cssRem)

		xlMax(self) {
			display(DisplayStyle.None)
		}
	}

	val tocLink by style {
		alignItems(AlignItems.Center)
		borderRadius(0.5.cssRem)
		color(LandingVars.Muted.value())
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
			backgroundColor(LandingVars.Accent.value().alpha(0.07))
			color(LandingVars.Text.value())
		}
	}

	val tocLinkActive by style {
		backgroundColor(LandingVars.Accent.value().alpha(0.12))
		color(LandingVars.Text.value())

		"svg" style {
			color(LandingVars.AccentStrong.value())
		}
	}

	val tocCount by style {
		color(LandingVars.Muted.value())
		fontSize(0.72.cssRem)
		marginLeft(autoLength)
		monoFont()
	}

	val header by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.6.cssRem)
		paddingBottom(1.cssRem)

		"h1" style {
			fontSize(2.6.cssRem)
			letterSpacing((-1).px)
			lineHeight(1.1)
			margin(0.px)
		}

		"p" style {
			color(LandingVars.Muted.value())
			fontSize(1.05.cssRem)
			margin(0.px)
			maxWidth(44.cssRem)
		}

		xlMax(self) {
			paddingTop(2.cssRem)
		}
	}

	val version by style {
		alignSelf(AlignSelf.FlexStart)
		color(LandingVars.Accent.value())
		fontSize(0.78.cssRem)
		monoFont()
		textDecorationLine(TextDecorationLine.None)
		transition(0.2.s, "color")

		hover(self) style {
			color(LandingVars.AccentStrong.value())
		}
	}

	val block by style {
		borderTop(1.px, LineStyle.Solid, LandingVars.Border.value())
		padding(2.8.cssRem, 0.px)
		// Clears the sticky header, and the table of contents strip under xl, when jumping to an anchor.
		scrollMarginTop(5.cssRem)

		xlMax(self) {
			scrollMarginTop(8.cssRem)
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
			fontSize(1.7.cssRem)
			gap(0.65.cssRem)
			letterSpacing((-0.4).px)
			margin(0.px)
			sansFont()
		}

		"h2 svg" style {
			color(LandingVars.Accent.value())
			flexShrink(0)
			fontSize(1.3.cssRem)
		}

		"p" style {
			color(LandingVars.Muted.value())
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
		backgroundColor(LandingVars.Border.value())
		display(DisplayStyle.Grid)
		gap(1.px)
		gridTemplateColumns("repeat(2, minmax(0, 1fr))")

		lgMax(self) {
			gridTemplateColumns("minmax(0, 1fr)")
		}
	}

	val codePanel by style {
		backgroundColor(LandingVars.Pane.value())
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
		color(LandingVars.Accent.value())
		monoLabel(0.75.cssRem)
	}

	val filePath by style {
		color(LandingVars.Muted.value())
		fontSize(0.78.cssRem)
		monoFont()
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
		borderTop(1.px, LineStyle.Solid, LandingVars.Border.value())
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
			columnGap(2.5.cssRem)
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
			alignSelf(AlignSelf.Start)
			position(Position.Sticky)
			top(8.cssRem)
		}
	}

	val linkRow by style {
		borderBottom(1.px, LineStyle.Solid, LandingVars.Border.value())
		color(LandingVars.Text.value())
		display(DisplayStyle.Grid)
		gap(1.cssRem)
		gridTemplateColumns("11rem minmax(0, 1fr)")
		padding(0.65.cssRem, 0.4.cssRem)
		textDecorationLine(TextDecorationLine.None)
		transition(0.2.s, "background-color", "padding")

		hover(self) style {
			backgroundColor(LandingVars.Accent.value().alpha(0.07))
			color(LandingVars.Text.value())
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

	/** The row or chip whose scene the stage shows, tinted so it's clear hovering them changes the visual. */
	val previewing by style {
		backgroundColor(LandingVars.Accent.value().alpha(0.12))
	}

	val chipPreviewable by style {
		borderStyle(LineStyle.Dashed)

		self + className(previewing) style {
			backgroundColor(LandingVars.Accent.value().alpha(0.12))
			borderColor(LandingVars.Accent.value().alpha(0.5))
		}
	}

	val stage by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.8.cssRem)
	}

	val sceneBody by style {
		animation(GlobalStyle.rise) {
			duration(0.35.s)
			timingFunction(AnimationTimingFunction.EaseOut)
		}
	}

	val sceneLink by style {
		alignItems(AlignItems.Center)
		alignSelf(AlignSelf.FlexStart)
		color(LandingVars.Muted.value())
		display(DisplayStyle.Flex)
		fontSize(0.88.cssRem)
		gap(0.4.cssRem)
		textDecorationLine(TextDecorationLine.None)
		transition(0.2.s, "color")

		hover(self) style {
			color(LandingVars.AccentStrong.value())
		}
	}

	val linkDescription by style {
		color(LandingVars.Muted.value())
		fontSize(0.9.cssRem)
		lineHeight(1.5)
	}

	val chipGrid by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.5.cssRem)
	}

	val chip by style {
		backgroundColor(LandingVars.Card.value())
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(0.6.cssRem)
		color(LandingVars.Text.value())
		fontSize(0.88.cssRem)
		padding(0.4.cssRem, 0.75.cssRem)
		textDecorationLine(TextDecorationLine.None)
	}

	val chipLinked by style {
		transition(0.2.s, "border-color", "background-color")

		hover(self) style {
			backgroundColor(LandingVars.Accent.value().alpha(0.12))
			borderColor(LandingVars.Accent.value().alpha(0.5))
			color(LandingVars.Text.value())
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
			color(LandingVars.Gold.value())
			fontSize(1.4.cssRem)
		}

		"h3" style {
			fontSize(1.1.cssRem)
			fontWeight(600)
			margin(0.6.cssRem, 0.px, 0.35.cssRem)
			sansFont()
		}

		"p" style {
			color(LandingVars.Muted.value())
			fontSize(0.93.cssRem)
			lineHeight(1.55)
			margin(0.px)
		}
	}

	val footnote by style {
		color(LandingVars.Muted.value())
		fontSize(0.9.cssRem)
		marginTop(1.5.cssRem)
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val cta by style {
		alignItems(AlignItems.Center)
		backgroundImage(
			linearGradient(120.deg) {
				add(LandingVars.Accent.value().alpha(0.12), 0.percent)
				add(LandingVars.Card.value(), 60.percent)
			}
		)
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
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
			color(LandingVars.Muted.value())
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
