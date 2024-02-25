package io.github.ayfri.kore.website.components.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.GlobalStyle
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

	val selectedTab = mutableStateOf(0)

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
					if (index == selectedTab.value) {
						classes(TabsStyle.selected)
					}

					onClick {
						selectedTab.value = index
					}
				}) {
					Text(tab.name)
				}
			}
		}

		Div({
			classes(TabsStyle.contentContainer)
		}) {
			tabs.mapIndexed { index, tab ->
				Div({
					if (index != selectedTab.value) hidden()
					classes(TabsStyle.content)
					contentClassName?.let { classes(it) }
				}) {
					tab.content()
				}
			}
		}
	}
}

object TabsStyle : StyleSheet() {
	val container by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		border(1.px, LineStyle.Solid, GlobalStyle.borderColor)
		borderRadius(GlobalStyle.roundingSection)
		overflow(Overflow.Hidden)
	}

	val buttons by style {
		display(DisplayStyle.Flex)
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		borderBottom(1.px, LineStyle.Solid, GlobalStyle.borderColor)
	}

	val button by style {
		backgroundColor(GlobalStyle.buttonBackgroundColor)
		cursor(Cursor.Pointer)
		color(GlobalStyle.textColor)
		padding(0.6.cssRem, 1.4.cssRem)

		border(0.px, LineStyle.Solid, GlobalStyle.borderColor)

		borderRight(1.px, LineStyle.Solid, GlobalStyle.borderColor)

		self + firstOfType style {
			borderTopLeftRadius(GlobalStyle.roundingSection)
		}
	}

	val selected by style {
		backgroundColor(GlobalStyle.buttonBackgroundColorHover)
	}

	val contentContainer by style {
		display(DisplayStyle.Flex)
		height(100.percent)
		overflowY(Overflow.Scroll)
		width(100.percent)
	}

	val content by style {}
}
