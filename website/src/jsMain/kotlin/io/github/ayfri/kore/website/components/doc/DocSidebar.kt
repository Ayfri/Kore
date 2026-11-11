package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.blur
import com.varabyte.kobweb.compose.css.functions.calc
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.icons.lucide.LucideX
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.layouts.PageTransitions
import io.github.ayfri.kore.website.docSections
import io.github.ayfri.kore.website.utils.smMax
import io.github.ayfri.kore.website.utils.transition
import io.github.ayfri.kore.website.utils.viewTransitionClass
import io.github.ayfri.kore.website.utils.viewTransitionName
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignSelf
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Nav

@Composable
fun DocSidebar(revealed: Boolean, onClose: () -> Unit) {
	Style(DocSidebarStyle)
	Style(DocTreeStyle)

	val path = rememberPageContext().route.path
	val section = docLocation(path)?.section ?: docSections.first()

	Nav({
		attr("aria-label", "Documentation")
		classes(DocSidebarStyle.sidebar, "sidebar")
		if (revealed) classes("reveal")
	}) {
		Div({
			classes(DocSidebarStyle.sidebarHeader)
		}) {
			Search(onNavigate = onClose)

			Button({
				attr("aria-label", "Close the navigation")
				classes(DocSidebarStyle.closeButton)
				onClick { onClose() }
			}) {
				LucideX()
			}
		}

		DocSectionSwitcher(section)
		DocTree(section, path)
	}
}

data object DocSidebarStyle : StyleSheet() {
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
		marginLeft(0.25.cssRem)
		/** The sticky offset plus 1.5rem kept free under the sidebar. */
		maxHeight(calc { 100.vh - 6.5.cssRem })
		padding(0.5.cssRem)
		position(Position.Sticky)
		top(5.cssRem)
		viewTransitionClass(PageTransitions.CHROME_CLASS)
		viewTransitionName(PageTransitions.DOC_SIDEBAR)
		width(22.cssRem)

		smMax(self) {
			backgroundColor(rgba(24, 26, 31, 0.95))
			backdropFilter(BackdropFilter.list(BackdropFilter.of(blur(4.px))))
			height(100.vh)
			left(0.px)
			maxHeight(MaxHeight.Unset)
			paddingTop(2.cssRem)
			position(Position.Fixed)
			top(0.px)
			translateX((-100).percent)
			transition(0.3.s, "translate", "visibility")
			/** Hidden and not only moved off screen, so the closed drawer's links stay out of the tab order. */
			visibility(Visibility.Hidden)
			width(100.percent)
			zIndex(60)
		}

		self + className("reveal") style {
			translateX(0.percent)
			visibility(Visibility.Visible)
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

		child(self, type("svg")) style {
			fontSize(1.75.cssRem)
		}
	}
}
