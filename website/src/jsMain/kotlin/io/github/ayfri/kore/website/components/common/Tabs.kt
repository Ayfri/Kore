package io.github.ayfri.kore.website.components.common

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.colorMix
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

data class Tab(
	val name: String,
	val content: @Composable () -> Unit,
)

@Composable
fun Tabs(tabs: List<Tab>, className: String? = null, contentClassName: String? = null) {
	Style(TabsStyle)

	var selectedTab by remember { mutableStateOf(0) }

	Div({
		classes(TabsStyle.container)
		className?.let { classes(it) }
	}) {
		Div({
			classes(TabsStyle.buttons)
		}) {
			tabs.forEachIndexed { index, tab ->
				Button({
					classes(TabsStyle.button)
					if (index == selectedTab) {
						classes(TabsStyle.selected)
					}

					onClick {
						selectedTab = index
					}
				}) {
					Text(tab.name)
				}
			}
		}

		Div({
			classes(TabsStyle.contentContainer)
		}) {
			tabs.forEachIndexed { index, tab ->
				key(index) {
					Div({
						if (index != selectedTab) hidden()
						classes(TabsStyle.content)
						contentClassName?.let { classes(it) }
					}) {
						tab.content()
					}
				}
			}
		}
	}
}

object TabsStyle : StyleSheet() {
	val container by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		border(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		borderRadius(1.2.cssRem)
		overflow(Overflow.Hidden)
		backgroundColor(Color("var(--landing-surface-2)"))
		property("box-shadow", "0 20px 50px rgba(5, 12, 20, 0.55)")
	}

	val buttons by style {
		display(DisplayStyle.Flex)
		backgroundColor(
			colorMix(
				ColorInterpolationMethod.Srgb,
				Color("var(--landing-surface)") to 55.percent,
				Color.transparent
			)
		)
		borderBottom(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		padding(0.35.cssRem)
		gap(0.35.cssRem)

		lgMax(self) {
			display(DisplayStyle.Grid)
			gridTemplateColumns("repeat(auto-fit, minmax(0, 1fr))")
		}

		smMax(self) {
			gridTemplateColumns("repeat(2, 1fr)")
		}

		xsMax(self) {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
		}
	}

	val button by style {
		backgroundColor(
			colorMix(
				ColorInterpolationMethod.Srgb,
				Color("var(--landing-text)") to 4.percent,
				Color.transparent
			)
		)
		borderRadius(0.4.cssRem)
		border(0.px)
		color(Color("var(--landing-muted)"))
		cursor(Cursor.Pointer)
		fontFamily("JetBrains Mono", "IBM Plex Mono", "Consolas", "monospace")
		fontSize(0.88.cssRem)
		fontWeight(500)
		letterSpacing(0.2.px)
		padding(0.4.cssRem, 0.85.cssRem)
		transition(0.2.s, "background-color", "color")

		hover(self) style {
			backgroundColor(
				colorMix(
					ColorInterpolationMethod.Srgb,
					Color("var(--landing-accent)") to 20.percent,
					Color.transparent
				)
			)
			color(Color("var(--landing-text)"))
		}

		lgMax(self) {
			fontSize(0.8.cssRem)
			paddingX(0.55.cssRem)
		}

		smMax(self) {
			fontSize(0.75.cssRem)
			paddingX(0.4.cssRem)
		}
	}

	val selected by style {
		backgroundColor(
			colorMix(
				ColorInterpolationMethod.Srgb,
				Color("var(--landing-accent)") to 30.percent,
				Color.transparent
			)
		)
		color(Color("var(--landing-text)"))
	}

	val contentContainer by style {
		display(DisplayStyle.Block)
		height(auto)
		width(100.percent)
	}

	val content by style {
		width(100.percent)
	}
}
