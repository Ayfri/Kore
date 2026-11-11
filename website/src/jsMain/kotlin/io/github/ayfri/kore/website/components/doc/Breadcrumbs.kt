package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.css.textDecorationLine
import com.varabyte.kobweb.silk.components.icons.lucide.LucideChevronRight
import com.varabyte.kobweb.silk.components.icons.lucide.LucideHouse
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.docEntries
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Nav
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

object BreadCrumbsStyle : StyleSheet() {
	val breadcrumbs by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		flexWrap(FlexWrap.Wrap)
		gap(0.5.cssRem)
		marginBottom(1.cssRem)
		marginTop(0.5.cssRem)
		opacity(0.8)

		"svg" style {
			flexShrink(0)
			fontSize(1.05.cssRem)
		}
	}

	val breadcrumbLink by style {
		display(DisplayStyle.Block)
		color(GlobalStyle.textColor)
		fontSize(0.9.cssRem)
		textDecorationLine(TextDecorationLine.None)
		transition(0.2.s, "color")

		self + hover style {
			color(GlobalStyle.linkColor)
		}
	}

	val breadcrumbText by style {
		color(GlobalStyle.inactiveLinkColor)
		display(DisplayStyle.Block)
		fontSize(0.9.cssRem)
	}
}

@Composable
fun Breadcrumbs(path: String) {
	Style(BreadCrumbsStyle)

	val location = docLocation(path)
	val article = docEntries.find { it.path == path }

	Nav({
		attr("aria-label", "Breadcrumb")
		classes(BreadCrumbsStyle.breadcrumbs)
	}) {
		A("/", {
			attr("aria-label", "Home")
			classes(BreadCrumbsStyle.breadcrumbLink)
		}) {
			LucideHouse()
		}

		if (location == null || article == null) return@Nav

		LucideChevronRight()
		A(location.section.firstPage.path, {
			classes(BreadCrumbsStyle.breadcrumbLink)
		}) {
			Text(location.section.title)
		}

		location.group?.let { group ->
			LucideChevronRight()
			Span({
				classes(BreadCrumbsStyle.breadcrumbText)
			}) {
				Text(group.title!!)
			}
		}

		LucideChevronRight()
		Span({
			attr("aria-current", "page")
			classes(BreadCrumbsStyle.breadcrumbText)
		}) {
			Text(article.navTitle)
		}
	}
}
