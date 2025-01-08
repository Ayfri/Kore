package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.cursor
import com.varabyte.kobweb.compose.css.translateY
import com.varabyte.kobweb.compose.css.zIndex
import com.varabyte.kobweb.silk.components.icons.mdi.MdiKeyboardArrowUp
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.transition
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button

object GoToTopButtonStyle : StyleSheet() {
	val button by style {
		alignItems(AlignItems.Center)
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		border(0.px)
		borderRadius(GlobalStyle.roundingButton)
		bottom(2.cssRem)
		color(GlobalStyle.textColor)
		cursor(Cursor.Pointer)
		display(DisplayStyle.Flex)
		height(3.cssRem)
		justifyContent(JustifyContent.Center)
		opacity(0)
		padding(0.px)
		position(Position.Fixed)
		right(2.cssRem)
		transition(0.3.s, "opacity", "translate")
		translateY(100.percent)
		width(3.cssRem)
		zIndex(10)

		self + hover style {
			backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		}

		self + className("visible") style {
			opacity(1)
			translateY(0.percent)
		}

		child(self, type("span")) style {
			fontSize(2.cssRem)
		}
	}
}

@Composable
fun GoToTopButton() {
	var visible by remember { mutableStateOf(false) }

	LaunchedEffect(Unit) {
		window.onscroll = {
			visible = window.scrollY > 300
		}
	}

	Style(GoToTopButtonStyle)

	Button(
		{
			classes(GoToTopButtonStyle.button)
			if (visible) classes("visible")
			onClick { window.scrollTo(0.0, 0.0) }
		}
	) {
		MdiKeyboardArrowUp()
	}
}
