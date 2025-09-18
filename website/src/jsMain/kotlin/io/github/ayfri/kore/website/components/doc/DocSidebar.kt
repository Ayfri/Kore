package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.silk.components.icons.mdi.MdiClose
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.smMax
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignSelf
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div

@Composable
fun DocSidebar(revealed: Boolean, onClose: () -> Unit) {
	Style(DocSidebarStyle)

	Div({
		classes(DocSidebarStyle.sidebar, "sidebar")
		if (revealed) classes("reveal")
	}) {
		Div({
			classes(DocSidebarStyle.sidebarHeader)
		}) {
			Search()

			Button({
				classes(DocSidebarStyle.closeButton)
				onClick { onClose() }
			}) {
				MdiClose()
			}
		}

		DocTree()
	}
}

object DocSidebarStyle : StyleSheet() {
	init {
		selector("body:has(.sidebar.reveal)") style {
			overflow(Overflow.Hidden)
		}
	}

	val sidebar by style {
        alignSelf(AlignSelf.FlexStart)
        backgroundColor(GlobalStyle.secondaryBackgroundColor)
        border(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor)
        borderRadius(GlobalStyle.roundingButton)
        display(DisplayStyle.Flex)
        flexDirection(FlexDirection.Column)
        gap(0.75.cssRem)
        padding(0.9.cssRem)
        position(Position.Sticky)
        top(5.cssRem)
        width(21.cssRem)

		smMax(self) {
            backgroundColor(rgba(24, 26, 31, 0.92))
            height(100.vh)
            left(0.px)
            paddingTop(2.cssRem)
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

	val sidebarHeader by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		justifyContent(JustifyContent.SpaceBetween)
		gap(0.5.cssRem)
	}

	val closeButton by style {
		backgroundColor(Color.transparent)
		border(0.px)
		color(GlobalStyle.textColor)
		cursor(Cursor.Pointer)
		display(DisplayStyle.None)
		padding(0.5.cssRem)

		smMax(self) {
			display(DisplayStyle.Block)
		}

		className("material-icons") style {
			fontSize(2.cssRem)
		}
	}
}
