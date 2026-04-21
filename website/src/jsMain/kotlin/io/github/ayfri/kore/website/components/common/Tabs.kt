package io.github.ayfri.kore.website.components.common

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.utils.mdMax
import io.github.ayfri.kore.website.utils.paddingX
import io.github.ayfri.kore.website.utils.transition
import io.github.ayfri.kore.website.utils.xsMax
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
		backgroundColor(Color("rgba(9, 14, 20, 0.9)"))
		borderBottom(1.px, LineStyle.Solid, Color("var(--landing-border)"))
		padding(0.5.cssRem)
		gap(0.5.cssRem)

		mdMax(self) {
			display(DisplayStyle.Grid)
			gridTemplateColumns("repeat(auto-fit, minmax(0, 1fr))")
		}

		xsMax(self) {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
		}
	}

	val button by style {
		backgroundColor(Color.transparent)
		cursor(Cursor.Pointer)
		color(Color("var(--landing-muted)"))
		padding(0.4.cssRem, 1.2.cssRem)
		fontFamily("JetBrains Mono", "IBM Plex Mono", "Consolas", "monospace")
		fontSize(0.88.cssRem)
		borderRadius(0.4.cssRem)
		fontWeight(500)
		letterSpacing(0.8.px)
		transition(0.2.s, "background-color", "color")

		border(0.px, LineStyle.Solid, Color.transparent)

		hover(self) style {
			backgroundColor(Color("rgba(8, 182, 214, 0.2)"))
			color(Color("var(--landing-text)"))
		}

		mdMax(self) {
			paddingX(0.8.cssRem)
		}
	}

	val selected by style {
		backgroundColor(Color("rgba(8, 182, 214, 0.3)"))
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
