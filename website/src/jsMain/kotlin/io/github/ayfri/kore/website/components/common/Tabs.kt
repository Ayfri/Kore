package io.github.ayfri.kore.website.components.common

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.LandingVars
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
		border(1.px, LineStyle.Solid, LandingVars.Border.value())
		borderRadius(1.2.cssRem)
		overflow(Overflow.Hidden)
		backgroundColor(LandingVars.Pane.value())
		boxShadow(0.px, 20.px, 50.px, 0.px, rgba(5, 12, 20, 0.55))
	}

	val buttons by style {
		display(DisplayStyle.Flex)
		backgroundColor(LandingVars.Surface.value().alpha(0.55))
		borderBottom(1.px, LineStyle.Solid, LandingVars.Border.value())
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
		backgroundColor(LandingVars.Text.value().alpha(0.04))
		border(0.px)
		borderRadius(0.4.cssRem)
		color(LandingVars.Muted.value())
		cursor(Cursor.Pointer)
		fontSize(0.88.cssRem)
		fontWeight(500)
		letterSpacing(0.2.px)
		monoFont()
		padding(0.4.cssRem, 0.85.cssRem)
		transition(0.2.s, "background-color", "color")

		hover(self) style {
			backgroundColor(LandingVars.Accent.value().alpha(0.2))
			color(LandingVars.Text.value())
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
		backgroundColor(LandingVars.Accent.value().alpha(0.35))
		color(LandingVars.Text.value())
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
