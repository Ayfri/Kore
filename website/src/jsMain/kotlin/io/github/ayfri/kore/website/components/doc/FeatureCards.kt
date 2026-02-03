package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.translateY
import com.varabyte.kobweb.silk.components.icons.mdi.MdiBook
import com.varabyte.kobweb.silk.components.icons.mdi.MdiDataObject
import com.varabyte.kobweb.silk.components.icons.mdi.MdiFunctions
import com.varabyte.kobweb.silk.components.icons.mdi.MdiSettings
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.marginY
import io.github.ayfri.kore.website.utils.mdMax
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Text

object FeatureStyle : StyleSheet() {
	val grid by style {
		display(DisplayStyle.Grid)
		gridTemplateColumns("repeat(2, 1fr)")
		gap(1.5.cssRem)
		margin(1.5.cssRem, 0.cssRem)

		mdMax(self) {
			gridTemplateColumns("1fr")
			gap(1.cssRem)
		}
	}

	val card by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		borderRadius(8.px)
		padding(2.cssRem, 1.5.cssRem)
		display(DisplayStyle.Grid)
		gridTemplateColumns("64px 1fr")
		gap(2.cssRem)
		transition(0.2.s, "translate")

		self + hover style {
			translateY((-4).px)
		}

		mdMax(self) {
			gap(1.5.cssRem)
			padding(1.cssRem)
		}
	}

	val icon by style {
		display(DisplayStyle.Flex)
		alignItems(AlignItems.Center)
		justifyContent(JustifyContent.Center)
		color(Color.white)

		"span" style {
			fontSize(48.px)
			opacity(0.8)

			mdMax(self) {
				fontSize(40.px)
			}
		}
	}

	val content by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		gap(1.cssRem)

		"h3" style {
			marginY(0.cssRem)
		}

		mdMax(self) {
			gap(0.5.cssRem)
		}
	}

	val featureLink by style {
		color(GlobalStyle.textColor)
		textDecoration("none")
		cursor("pointer")
		transition(0.2.s, "color")

		self + hover style {
			color(GlobalStyle.linkColor)
		}
	}

	val linkList by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
	}
}

@Composable
private fun FeatureIcon(content: @Composable () -> Unit) {
	Div({
		classes(FeatureStyle.icon)
	}) {
		content()
	}
}

@Composable
private fun FeatureCard(
	title: String,
	icon: @Composable () -> Unit,
	content: @Composable () -> Unit,
) {
	Div({
		classes(FeatureStyle.card)
	}) {
		FeatureIcon(icon)
		Div({
			classes(FeatureStyle.content)
		}) {
			H3 { Text(title) }
			content()
		}
	}
}

@Composable
private fun FeatureLink(href: String, text: String) {
	A(href = href, {
		classes(FeatureStyle.featureLink)
	}) {
		Text(text)
	}
}

@Composable
fun FeatureGrid() {
	Style(FeatureStyle)

	Div({
		classes(FeatureStyle.grid)
	}) {
		FeatureCard(
			title = "Getting Started",
			icon = { MdiBook() }
		) {
			Div({
				classes(FeatureStyle.linkList)
			}) {
				FeatureLink("/docs/getting-started", "Getting Started")
				FeatureLink("/docs/guides/creating-a-datapack", "Creating a Datapack")
				FeatureLink("/docs/guides/configuration", "Configuration")
			}
		}

		FeatureCard(
			title = "Commands & Functions",
			icon = { MdiFunctions() }
		) {
			Div({
				classes(FeatureStyle.linkList)
			}) {
				FeatureLink("/docs/commands/", "Commands")
				FeatureLink("/docs/commands/functions", "Functions")
				FeatureLink("/docs/commands/macros", "Macros")
				FeatureLink("/docs/helpers/scheduler", "Scheduler")
			}
		}

		FeatureCard(
			title = "Data-driven Features",
			icon = { MdiDataObject() }
		) {
			Div({
				classes(FeatureStyle.linkList)
			}) {
				FeatureLink("/docs/data-driven/predicates", "Predicates")
				FeatureLink("/docs/data-driven/loot-tables", "Loot Tables")
				FeatureLink("/docs/data-driven/recipes", "Recipes")
				FeatureLink("/docs/data-driven/advancements", "Advancements")
				FeatureLink("/docs/data-driven/enchantments", "Enchantments")
				FeatureLink("/docs/data-driven/dialogs", "Dialogs")
			}
		}

		FeatureCard(
			title = "Concepts & Helpers",
			icon = { MdiSettings() }
		) {
			Div({
				classes(FeatureStyle.linkList)
			}) {
				FeatureLink("/docs/concepts/components", "Components")
				FeatureLink("/docs/concepts/chat-components", "Chat Components")
				FeatureLink("/docs/concepts/scoreboards", "Scoreboards")
				FeatureLink("/docs/helpers/display-entities", "Display Entities")
				FeatureLink("/docs/helpers/mannequins", "Mannequins")
				FeatureLink("/docs/advanced/known-issues", "Known Issues")
			}
		}
	}
}
