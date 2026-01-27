package io.github.ayfri.kore.website.components.common

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.css.*
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
		border(1.px, LineStyle.Solid, GlobalStyle.borderColor.alpha(0.3))
		borderRadius(GlobalStyle.roundingSection)
		overflow(Overflow.Hidden)
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		property("box-shadow", "0 20px 50px ${GlobalStyle.shadowColor.alpha(0.5)}")
	}

	val buttons by style {
		display(DisplayStyle.Flex)
		backgroundColor(Color("#1e2227"))
		borderBottom(1.px, LineStyle.Solid, GlobalStyle.borderColor.alpha(0.2))
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
		color(GlobalStyle.altTextColor)
		padding(0.4.cssRem, 1.2.cssRem)
		fontFamily("inherit")
		borderRadius(0.4.cssRem)
		fontWeight(500)
		transition(0.2.s, "background-color", "color")

		border(0.px, LineStyle.Solid, Color.transparent)

		hover(self) style {
			backgroundColor(GlobalStyle.tertiaryBackgroundColor.alpha(0.5))
			color(GlobalStyle.textColor)
		}

		mdMax(self) {
			paddingX(0.8.cssRem)
		}
	}

	val selected by style {
		backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		color(GlobalStyle.textColor)
	}

	val contentContainer by style {
		display(DisplayStyle.Flex)
		height(100.percent)
		overflowY(Overflow.Auto)
		width(100.percent)
	}

	val content by style {}
}
