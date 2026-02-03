package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.css.textDecorationLine
import com.varabyte.kobweb.silk.components.icons.mdi.MdiChevronRight
import com.varabyte.kobweb.silk.components.icons.mdi.MdiHome
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.docEntries
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

object BreadCrumbsStyle : StyleSheet() {
	val breadcrumbs by style {
		alignItems(AlignItems.Center)
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Row)
		gap(0.5.cssRem)
		marginBottom(1.cssRem)
		marginTop(0.5.cssRem)
		opacity(0.8)

		child(self, className("material-icons")) style {
			fontSize(1.2.cssRem)
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
fun Breadcrumbs(slugs: List<String>) {
	Style(BreadCrumbsStyle)

	Div({
		classes(BreadCrumbsStyle.breadcrumbs)
	}) {
		A("/", {
			classes(BreadCrumbsStyle.breadcrumbLink)
		}) {
			MdiHome()
		}

		slugs.forEachIndexed { index, slug ->
			MdiChevronRight()
			val path = "/docs/${slugs.take(index + 1).joinToString("/")}"
			val displayText = slug.replace("-", " ").replaceFirstChar { it.uppercase() }
			val pathExists = docEntries.any { it.path == path }
			if (pathExists) {
				A(path, {
					classes(BreadCrumbsStyle.breadcrumbLink)
				}) {
					Text(displayText)
				}
			} else {
				Span({
					classes(BreadCrumbsStyle.breadcrumbText)
				}) {
					Text(displayText)
				}
			}
		}
	}
}
