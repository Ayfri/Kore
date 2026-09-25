package io.github.ayfri.kore.website.components.index

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.linearGradient
import com.varabyte.kobweb.silk.components.icons.lucide.LucideArrowRight
import io.github.ayfri.kore.website.components.common.BrandIcon
import io.github.ayfri.kore.website.components.common.BrandIconStyle
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.*

private data class Tool(
	val title: String,
	val description: String,
	val icon: String,
	val link: String,
	val linkLabel: String,
)

private val tools = listOf(
	Tool(
		"IntelliJ IDEA plugin",
		"Kore Assistant shows every function, advancement and loot table as a datapack tree, flags calls to functions declared nowhere, and builds your pack from the run marker on dataPack().",
		"intellijidea",
		"https://plugins.jetbrains.com/plugin/27025-kore-assistant",
		"JetBrains Marketplace",
	),
	Tool(
		"VS Code extension",
		"Gutter icons on every datapack and function, hovers and jump to declaration, so the Kore DSL feels native in VS Code too.",
		"visualstudiocode",
		"https://marketplace.visualstudio.com/items?itemName=ayfri.kore-assistant",
		"VS Code Marketplace",
	),
	Tool(
		"Gradle plugin",
		"gradlew koreRun --continuous regenerates the pack on every save, copies it into your worlds and reloads your server over RCON. No alt-tab, no /reload.",
		"gradle",
		"/docs/guides/gradle-plugin",
		"Read the guide",
	),
)

@Composable
fun ToolingSection() {
	Style(BrandIconStyle)
	Style(ToolingSectionStyle)

	Div({
		classes(ToolingSectionStyle.container)
	}) {
		H2({
			classes(ToolingSectionStyle.sectionTitle)
		}) {
			Text("Tooling that fits your workflow")
		}

		P(
			"Plugins for your editor and your build, so writing, checking and testing a pack all happen without leaving your IDE.",
			ToolingSectionStyle.sectionSubtitle
		)

		Div({
			classes(ToolingSectionStyle.grid)
		}) {
			tools.forEach { tool ->
				A(tool.link, {
					classes(ToolingSectionStyle.card)
					if (tool.link.startsWith("http")) target(ATarget.Blank)
				}) {
					Div({
						classes(ToolingSectionStyle.cardHeader)
					}) {
						Div({
							classes(ToolingSectionStyle.cardIcon)
						}) {
							BrandIcon(tool.icon)
						}
						H3 {
							Text(tool.title)
						}
					}

					P(tool.description)

					Span({
						classes(ToolingSectionStyle.cardLink)
					}) {
						Text(tool.linkLabel)
						LucideArrowRight()
					}
				}
			}
		}
	}
}

object ToolingSectionStyle : StyleSheet() {
	val container by style {
		marginX(auto)
		maxWidth(85.cssRem)
		padding(2.5.cssRem, 5.vw)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
	}

	val sectionTitle by style {
		fontSize(2.6.cssRem)
		marginTop(0.px)
		marginBottom(0.5.cssRem)

		smMax(self) {
			fontSize(2.cssRem)
		}
	}

	val sectionSubtitle by style {
		color(Color("var(--landing-muted)"))
		fontSize(1.1.cssRem)
		lineHeight(1.6.number)
		marginBottom(1.8.cssRem)
		maxWidth(42.cssRem)
	}

	val grid by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns("repeat(3, minmax(0, 1fr))")
		gap(1.2.cssRem)

		lgMax(self) {
			gridTemplateColumns("minmax(0, 1fr)")
		}
	}

	@OptIn(ExperimentalComposeWebApi::class)
	val card by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(1.2.cssRem)
		padding(1.5.cssRem, 1.6.cssRem)
		backgroundImage(
			linearGradient(135.deg) {
				add(rgba(21, 28, 38, 0.95), 0.percent)
				add(rgba(15, 20, 27, 0.92), 60.percent)
				add(rgba(8, 182, 214, 0.09), 100.percent)
			}
		)
		boxShadow(0.px, 18.px, 40.px, 0.px, rgba(5, 12, 20, 0.35))
		color(Color("var(--landing-text)"))
		textDecorationLine(TextDecorationLine.None)
		transition(0.35.s, "transform", "border-color", "box-shadow")

		hover(self) style {
			transform { translateY((-6).px) }
			borderColor(Color("rgba(8, 182, 214, 0.55)"))
			boxShadow(0.px, 26.px, 65.px, 0.px, rgba(5, 12, 20, 0.5))
		}

		"p" {
			color(Color("var(--landing-muted)"))
			flexGrow(1)
			fontSize(1.02.cssRem)
			lineHeight(1.6.number)
			marginTop(0.9.cssRem)
			marginBottom(1.1.cssRem)
		}

		smMax(self) {
			padding(1.3.cssRem)
		}
	}

	val cardHeader by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		gap(0.9.cssRem)

		"h3" {
			fontSize(1.35.cssRem)
			lineHeight(1.3.number)
			margin(0.px)
		}
	}

	val cardIcon by style {
		width(2.6.cssRem)
		height(2.6.cssRem)
		flexShrink(0)
		borderRadius(0.85.cssRem)
		border(1.px, LineStyle.Solid, Color("rgba(8, 182, 214, 0.3)"))
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		justifyContent(JustifyContent.Center)
		backgroundImage(
			linearGradient(160.deg) {
				add(rgba(8, 182, 214, 0.24), 0.percent)
				add(rgba(8, 182, 214, 0.06), 100.percent)
			}
		)
		color(Color("var(--landing-accent-strong)"))
		fontSize(1.35.cssRem)
	}

	val cardLink by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		gap(0.4.cssRem)
		color(Color("var(--landing-accent)"))
		fontWeight(FontWeight.Bold)
	}
}
