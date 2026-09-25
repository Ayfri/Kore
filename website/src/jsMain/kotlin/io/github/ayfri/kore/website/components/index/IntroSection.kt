package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.components.icons.lucide.LucideArrowRight
import com.varabyte.kobweb.silk.components.icons.lucide.LucidePackageCheck
import com.varabyte.kobweb.silk.components.icons.lucide.LucideRepeat
import com.varabyte.kobweb.silk.components.icons.lucide.LucideShieldCheck
import io.github.ayfri.kore.website.components.features.FeatureSectionsStyle
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.A as DomA

private class Benefit(val title: String, val description: String, val icon: @Composable () -> Unit)

private val benefits = listOf(
	Benefit(
		"Mistakes caught early",
		"Every item, block, sound and command comes typed from Minecraft's own data. A typo fails the build, not your /reload.",
	) { LucideShieldCheck() },
	Benefit(
		"Reuse instead of copy-paste",
		"Functions, loops and variables write the repetitive parts for you. Rename something and every reference follows.",
	) { LucideRepeat() },
	Benefit(
		"Plain vanilla output",
		"Kore only runs when you build. What ships is a regular datapack, with nothing to install on the server.",
	) { LucidePackageCheck() },
)

@Composable
fun IntroSection() {
	Style(IntroSectionStyle)

	Section({ classes(FeatureSectionsStyle.section) }) {
		SectionHeader(
			"Datapacks are code. Write them like code.",
			"A datapack is the folder of commands and JSON files Minecraft loads to add recipes, loot, advancements, minigames or whole new worlds, no mod required. Kore lets you build one with a real programming language instead of by hand.",
		)

		Div({ classes(IntroSectionStyle.grid) }) {
			benefits.forEach { benefit ->
				Div({ classes(IntroSectionStyle.benefit) }) {
					Span({ classes(IntroSectionStyle.icon) }) { benefit.icon() }
					H3 { Text(benefit.title) }
					P(benefit.description)
				}
			}
		}

		Div({ classes(IntroSectionStyle.more) }) {
			DomA("/docs/guides/why-kore") {
				Text("How Kore compares to hand-written packs, Sandstone and beet")
				LucideArrowRight()
			}
		}
	}
}

object IntroSectionStyle : StyleSheet() {
	val grid by style {
		display(DisplayStyle.Grid)
		gap(2.5.cssRem)
		gridTemplateColumns("repeat(3, minmax(0, 1fr))")

		mdMax(self) {
			gap(2.cssRem)
			gridTemplateColumns("minmax(0, 1fr)")
		}
	}

	val benefit by style {
		borderTop(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		paddingTop(1.5.cssRem)

		"h3" style {
			fontSize(1.2.cssRem)
			margin(1.cssRem, 0.px, 0.5.cssRem)
		}

		"p" style {
			color(Color("var(--landing-muted)"))
			fontSize(1.cssRem)
			lineHeight(1.6.number)
			margin(0.px)
		}
	}

	val icon by style {
		alignItems(AlignItems.Center)
		backgroundColor(rgba(8, 182, 214, 0.12))
		borderRadius(0.6.cssRem)
		color(Color("var(--landing-accent-strong)"))
		display(DisplayStyle.Flex)
		fontSize(1.2.cssRem)
		height(2.4.cssRem)
		justifyContent(JustifyContent.Center)
		width(2.4.cssRem)
	}

	val more by style {
		display(DisplayStyle.Flex)
		justifyContent(JustifyContent.Center)
		marginTop(3.cssRem)

		"a" style {
			alignItems(AlignItems.Center)
			display(DisplayStyle.Flex)
			fontWeight(500)
			gap(0.4.cssRem)
		}
	}
}
