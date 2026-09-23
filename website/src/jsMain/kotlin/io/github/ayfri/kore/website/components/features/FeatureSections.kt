package io.github.ayfri.kore.website.components.features

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.kobweb.silk.components.icons.lucide.LucideArrowUpRight
import com.varabyte.kobweb.silk.components.icons.lucide.LucideCircleX
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.*
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.A as DomA

@Composable
fun FeaturesHero() {
	Section({ classes(FeatureSectionsStyle.hero) }) {
		Span("Features", FeatureSectionsStyle.eyebrow)

		H1({ classes(FeatureSectionsStyle.heroTitle) }) {
			Text("Everything you can build with ")
			Span("Kore", FeatureSectionsStyle.heroTitleAccent)
		}

		P(
			"Commands, every data-driven JSON file, world generation, gameplay systems and the tooling to ship them. All typed, all generated from Minecraft's own data, all compiled to plain vanilla datapacks.",
			FeatureSectionsStyle.heroSubtitle
		)

		Div({ classes(FeatureSectionsStyle.stats) }) {
			featureStats.forEach { stat ->
				Div({ classes(FeatureSectionsStyle.stat) }) {
					Span(stat.value, FeatureSectionsStyle.statValue)
					Span(stat.label, FeatureSectionsStyle.statLabel)
				}
			}
		}

		Div({ classes(FeatureSectionsStyle.heroActions) }) {
			LinkButton("Get Started", "/docs/getting-started", color = ButtonColor.PRIMARY)
			LinkButton("Compare with other tools", "/docs/guides/why-kore", variant = ButtonVariant.OUTLINE)
		}

		Nav({ classes(FeatureSectionsStyle.nav) }) {
			featureCategories.forEach { category ->
				DomA("#${category.id}", { classes(FeatureSectionsStyle.navLink) }) { Text(category.title) }
			}
			DomA("#limits", { classes(FeatureSectionsStyle.navLink) }) { Text("Limits") }
		}
	}
}

@Composable
fun ShowcaseSection() {
	Section({
		id("showcase")
		classes(FeatureSectionsStyle.showcase)
	}) {
		H2({ classes(FeatureSectionsStyle.centeredTitle) }) { Text("Kotlin in, vanilla datapack out") }
		P("What you write on the left, the exact files Kore generates on the right. Nothing else ships.", FeatureSectionsStyle.centeredTagline)

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

/**
 * A category with scenes renders as a zigzag row whose stage previews the scene of the hovered item,
 * one without renders its items as a full-width bento grid.
 */
@Composable
fun CategorySection(category: FeatureCategory, position: Int) {
	val scenes = category.scenes
	var active by remember { mutableStateOf(0) }
	val stageId = "stage-${category.id}"
	if (scenes.isNotEmpty()) highlightCodeIn(stageId, active)

	Section({
		id(category.id)
		classes(FeatureSectionsStyle.section)
		if (scenes.isNotEmpty()) classes(FeatureSectionsStyle.row)
		if (position % 2 == 1) classes(FeatureSectionsStyle.rowReversed)
	}) {
		if (scenes.isEmpty()) {
			Div({ classes(FeatureSectionsStyle.centeredKicker) }) { Kicker(category) }
			H2({ classes(FeatureSectionsStyle.centeredTitle) }) { Text(category.headline) }
			P(category.tagline, FeatureSectionsStyle.centeredTagline)
			Div({ classes(FeatureSectionsStyle.bento) }) {
				category.items.forEach { BentoCard(it) }
			}
			return@Section
		}

		Div({ classes(FeatureSectionsStyle.rowHead) }) {
			Kicker(category)
			H2({ classes(FeatureSectionsStyle.rowTitle) }) { Text(category.headline) }
			P(category.tagline, FeatureSectionsStyle.rowTagline)
		}

		Div({ classes(FeatureSectionsStyle.rowDetails) }) {
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

		Div({ classes(FeatureSectionsStyle.rowVisual) }) {
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

@Composable
fun LimitsSection() {
	Section({
		id("limits")
		classes(FeatureSectionsStyle.section)
	}) {
		H2({ classes(FeatureSectionsStyle.centeredTitle) }) { Text("What Kore does not do") }
		P("Knowing the boundaries saves you time before you pick a tool.", FeatureSectionsStyle.centeredTagline)

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
private fun Kicker(category: FeatureCategory) {
	Span({ classes(FeatureSectionsStyle.kicker) }) {
		category.icon()
		Text(category.title)
	}
}

@Composable
private fun BentoCard(item: FeatureItem) {
	DomA(item.href, {
		classes(FeatureSectionsStyle.bentoCard)
		if (item.href.startsWith("http")) target(ATarget.Blank)
	}) {
		H3 {
			Span({ classes(FeatureSectionsStyle.bentoTitle) }) {
				item.logos.forEach { BrandIcon(it) }
				Text(item.name)
			}
			LucideArrowUpRight()
		}
		P(item.description)
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

	val hero by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.2.cssRem)
		padding(4.5.cssRem, 6.vw, 2.cssRem)
		textAlign(TextAlign.Center)

		smMax(self) {
			padding(3.cssRem, 1.1.cssRem, 1.5.cssRem)
		}
	}

	val eyebrow by style {
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(999.px)
		color(Color("var(--landing-accent)"))
		fontFamily(MONO, "monospace")
		fontSize(0.8.cssRem)
		letterSpacing(2.px)
		padding(0.35.cssRem, 0.9.cssRem)
		textTransform(TextTransform.Uppercase)
	}

	val heroTitle by style {
		fontSize(3.4.cssRem)
		letterSpacing((-1.5).px)
		lineHeight(1.08.number)
		margin(0.px)
		maxWidth(50.cssRem)
		textWrap(TextWrap.Balance)

		mdMax(self) {
			fontSize(2.4.cssRem)
		}
	}

	val heroTitleAccent by style {
		textGradient(GlobalStyle.logoRightColor, GlobalStyle.logoLeftColor)
	}

	val heroSubtitle by style {
		color(Color("var(--landing-muted)"))
		fontSize(1.15.cssRem)
		margin(0.px)
		maxWidth(44.cssRem)
		textWrap(TextWrap.Pretty)
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

	val heroActions by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(1.cssRem)
		justifyContent(JustifyContent.Center)
		marginTop(0.6.cssRem)
	}

	val nav by style {
		display(DisplayStyle.Flex)
		flexWrap(FlexWrap.Wrap)
		gap(0.5.cssRem)
		justifyContent(JustifyContent.Center)
		marginTop(1.6.cssRem)
		maxWidth(56.cssRem)
	}

	val navLink by style {
		backgroundColor(rgba(21, 28, 38, 0.6))
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(999.px)
		color(Color("var(--landing-muted)"))
		fontSize(0.88.cssRem)
		padding(0.35.cssRem, 0.9.cssRem)
		textDecorationLine(TextDecorationLine.None)
		transition(0.2.s, "color", "border-color", "background-color")

		hover(self) style {
			backgroundColor(rgba(8, 182, 214, 0.12))
			borderColor(Color("rgba(8, 182, 214, 0.5)"))
			color(Color("var(--landing-text)"))
		}
	}

	val showcase by style {
		boxSizing(BoxSizing.BorderBox)
		marginX(auto)
		maxWidth(80.cssRem)
		padding(2.cssRem, 5.vw, 3.cssRem)
		width(100.percent)

		smMax(self) {
			padding(1.5.cssRem, 1.1.cssRem, 2.cssRem)
		}
	}

	val centeredTitle by style {
		fontFamily(SANS, "sans-serif")
		fontSize(2.3.cssRem)
		letterSpacing((-0.5).px)
		margin(0.px, 0.px, 0.5.cssRem)
		textAlign(TextAlign.Center)

		smMax(self) {
			fontSize(1.7.cssRem)
		}
	}

	val centeredTagline by style {
		color(Color("var(--landing-muted)"))
		fontSize(1.05.cssRem)
		marginBottom(2.cssRem)
		marginTop(0.px)
		marginX(auto)
		maxWidth(40.cssRem)
		textAlign(TextAlign.Center)
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

	val section by style {
		boxSizing(BoxSizing.BorderBox)
		marginX(auto)
		maxWidth(80.cssRem)
		padding(4.5.cssRem, 5.vw)
		// Clears the sticky header when jumping to an anchor.
		property("scroll-margin-top", "4.5rem")
		width(100.percent)

		smMax(self) {
			padding(3.cssRem, 1.1.cssRem)
		}
	}

	// On mobile the visual moves between the pitch and the details, so it isn't buried under a long list.
	val row by style {
		display(DisplayStyle.Grid)
		gap(1.6.cssRem, 4.cssRem)
		gridTemplateAreas("head visual", "details visual")
		gridTemplateColumns("minmax(0, 1fr) minmax(0, 1.1fr)")
		gridTemplateRows("auto 1fr")

		lgMax(self) {
			gap(1.8.cssRem)
			gridTemplateAreas("head", "visual", "details")
			gridTemplateColumns("minmax(0, 1fr)")
		}
	}

	val rowReversed by style {
		lgMin(self) {
			gridTemplateAreas("visual head", "visual details")
			gridTemplateColumns("minmax(0, 1.1fr) minmax(0, 1fr)")
		}
	}

	val rowHead by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
		gridArea("head")
		minWidth(0.px)
	}

	val rowDetails by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)
		gridArea("details")
		minWidth(0.px)
	}

	val centeredKicker by style {
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.Center)
		marginBottom(0.8.cssRem)
	}

	val rowVisual by style {
		gridArea("visual")
		minWidth(0.px)

		lgMin(self) {
			property("align-self", "start")
			position(Position.Sticky)
			top(7.cssRem)
		}
	}

	val kicker by style {
		alignItems(AlignItems.Center)
		property("align-self", "flex-start")
		color(Color("var(--landing-accent)"))
		display(DisplayStyle.Flex)
		fontFamily(MONO, "monospace")
		fontSize(0.8.cssRem)
		gap(0.5.cssRem)
		letterSpacing(1.5.px)
		textTransform(TextTransform.Uppercase)

		"svg" style {
			fontSize(1.05.cssRem)
		}
	}

	val rowTitle by style {
		fontFamily(SANS, "sans-serif")
		fontSize(2.3.cssRem)
		letterSpacing((-0.6).px)
		lineHeight(1.15.number)
		margin(0.px)
		textWrap(TextWrap.Balance)

		smMax(self) {
			fontSize(1.7.cssRem)
		}
	}

	val rowTagline by style {
		color(Color("var(--landing-muted)"))
		fontSize(1.08.cssRem)
		margin(0.px)
	}

	val linkList by style {
		borderTop(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
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

	val bentoTitle by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		gap(0.55.cssRem)

		"span" style {
			height(1.3.cssRem)
			width(1.3.cssRem)
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

	val bento by style {
		display(DisplayStyle.Grid)
		gap(1.cssRem)
		gridTemplateColumns("repeat(3, minmax(0, 1fr))")

		// Cards 1, 4 and 5 span two columns, so six tiles fill a 3x3 grid unevenly instead of reading as a list.
		"> :is(:nth-child(1), :nth-child(4), :nth-child(5))" style { property("grid-column", "span 2") }

		lgMax(self) {
			gridTemplateColumns("repeat(2, minmax(0, 1fr))")
			"> *" style { property("grid-column", "span 1") }
		}

		smMax(self) {
			gridTemplateColumns("minmax(0, 1fr)")
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val bentoCard by style {
		backgroundImage(
			linearGradient(145.deg) {
				add(rgba(8, 182, 214, 0.08), 0.percent)
				add(rgba(21, 28, 38, 0.95), 45.percent)
				add(rgba(15, 20, 27, 0.95), 100.percent)
			}
		)
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(1.2.cssRem)
		color(Color("var(--landing-text)"))
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(0.6.cssRem)
		minHeight(9.cssRem)
		padding(1.5.cssRem)
		textDecorationLine(TextDecorationLine.None)
		transition(0.25.s, "transform", "border-color", "box-shadow")

		"h3" style {
			alignItems(AlignItems.Center)
			display(DisplayStyle.Flex)
			fontFamily(SANS, "sans-serif")
			fontSize(1.2.cssRem)
			fontWeight(600)
			justifyContent(JustifyContent.SpaceBetween)
			margin(0.px)
		}

		"svg" style {
			color(Color("var(--landing-muted)"))
			flexShrink(0)
			transition(0.25.s, "color", "transform")
		}

		"p" style {
			color(Color("var(--landing-muted)"))
			fontSize(0.95.cssRem)
			lineHeight(1.55.number)
			margin(0.px)
		}

		hover(self) style {
			borderColor(Color("rgba(8, 182, 214, 0.55)"))
			boxShadow(0.px, 18.px, 45.px, 0.px, rgba(5, 12, 20, 0.45))
			color(Color("var(--landing-text)"))
			transform { translateY((-3).px) }
		}

		desc(hover(self), type("svg")) style {
			color(Color("var(--landing-accent-strong)"))
			transform { translate(2.px, (-2).px) }
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
		textAlign(TextAlign.Center)
	}
}
