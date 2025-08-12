package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.cursor
import com.varabyte.kobweb.compose.css.translateX
import com.varabyte.kobweb.compose.css.zIndex
import com.varabyte.kobweb.silk.components.icons.mdi.MdiClose
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.smMax
import io.github.ayfri.kore.website.utils.smMin
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div

@Composable
fun DocSidebar(revealed: Boolean, onClose: () -> Unit) {
	Style(DocSidebarStyle)

	Div({
		classes(DocSidebarStyle.sidebar)
		if (revealed) classes("reveal")
	}) {
		Button({
			classes(DocSidebarStyle.closeButton)
			onClick { onClose() }
		}) {
			MdiClose()
		}

		Search()
		DocTree()
	}
}

object DocSidebarStyle : StyleSheet() {
	val sidebar by style {
		backgroundColor(GlobalStyle.secondaryBackgroundColor)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		padding(0.75.cssRem)
		position(Position.Relative)

		smMin(self) {
			borderRadius(GlobalStyle.roundingButton)
		}

		smMax(self) {
			backgroundColor(GlobalStyle.backgroundColor)
			height(100.vh)
			left(0.px)
			paddingBottom(100.vh)
			paddingTop(4.cssRem)
			position(Position.Fixed)
			top(0.px)
			translateX((-100).percent)
			transition(0.3.s, "translate")
			width(100.percent)
			zIndex(60)
		}

		self + className("reveal") style {
			translateX(0.percent)
		}
	}

	val closeButton by style {
		backgroundColor(Color.transparent)
		border(0.px)
		color(GlobalStyle.textColor)
		cursor(Cursor.Pointer)
		display(DisplayStyle.None)
		padding(0.5.cssRem)
		position(Position.Absolute)
		right(1.cssRem)
		top(1.cssRem)

		smMax(self) {
			display(DisplayStyle.Block)
		}

		className("material-icons") style {
			fontSize(2.cssRem)
		}
	}
}
