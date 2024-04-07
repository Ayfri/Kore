package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.ListStyleType
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.css.listStyle
import com.varabyte.kobweb.compose.css.whiteSpace
import com.varabyte.kobweb.core.rememberPageContext
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.docEntries
import io.github.ayfri.kore.website.utils.A
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Ul

@Composable
fun Entry(article: DocArticle, selected: Boolean = false) = Li {
	A(article.path, article.navTitle) {
		classes(DocTreeStyle.entry)
		if (selected) classes(DocTreeStyle.selected)
	}
}

@Composable
fun DocTree() {
	Style(DocTreeStyle)

	val context = rememberPageContext().route
	val entries = docEntries
	val currentURL = context.path

	Ul({
		classes(DocTreeStyle.list)
	}) {
		entries.forEach { entry ->
			Entry(entry, entry.path == currentURL)
		}
	}
}

object DocTreeStyle : StyleSheet() {
	val list by style {
		listStyle(ListStyleType.None)
		padding(0.8.cssRem)
		marginRight(1.cssRem)

		height(100.vh)
		position(Position.Sticky)
		top(0.px)
		left(0.px)
	}

	val entry by style {
		display(DisplayStyle.Block)
		padding(0.5.cssRem)

		borderRadius(GlobalStyle.roundingButton)
		fontSize(1.2.cssRem)

		transition(0.2.s, "background-color")

		self + hover style {
			backgroundColor(GlobalStyle.tertiaryBackgroundColor)
		}

		color(GlobalStyle.textColor)
		whiteSpace(WhiteSpace.NoWrap)

	}

	val selected by style {
		color(GlobalStyle.linkColor)
	}
}
