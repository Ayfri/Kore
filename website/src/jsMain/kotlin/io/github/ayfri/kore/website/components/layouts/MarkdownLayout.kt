package io.github.ayfri.kore.website.components.layouts

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.overflowX
import com.varabyte.kobweb.compose.css.textAlign
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobwebx.markdown.markdown
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.setDescription
import io.github.ayfri.kore.website.components.doc.DocTree
import io.github.ayfri.kore.website.utils.marginX
import io.github.ayfri.kore.website.utils.marginY
import io.github.ayfri.kore.website.utils.smMax
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div

@Composable
fun MarkdownLayout(content: @Composable () -> Unit) {
	Style(MarkdownLayoutStyle)

	val context = rememberPageContext()
	val markdownData = context.markdown!!.frontMatter

	PageLayout(markdownData["nav-title"]?.get(0) ?: "Untitled") {
		DocTree()

		Div({
			classes(MarkdownLayoutStyle.content)
		}) {
			if (markdownData["description"] != null) {
				setDescription(markdownData["description"]!![0])
			}

			content()
		}
	}
}

object MarkdownLayoutStyle : StyleSheet() {
	init {
		id("root") style {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Column)
			minHeight(100.vh)
		}

		"main" style {
			display(DisplayStyle.Flex)
			flexDirection(FlexDirection.Row)
			flexGrow(1.0)
		}

		"h1" style {
			fontSize(3.cssRem)
			marginY(3.cssRem)
			textAlign(TextAlign.Center)
		}

		"h2" style {
			fontSize(2.cssRem)
			marginTop(2.5.cssRem)
			marginBottom(1.5.cssRem)
		}

		child(type("div") + className("code-toolbar"), type("pre")) style {
			borderRadius(GlobalStyle.roundingButton)
		}

		smMax {
			"h1" {
				fontSize(2.5.cssRem)
				marginY(2.cssRem)
			}

			"h2" {
				fontSize(2.cssRem)
			}
		}
	}

	val content by style {
		display(DisplayStyle.Flex)
		flexDirection(FlexDirection.Column)
		marginX(3.vw)
		marginBottom(2.cssRem)
		minHeight(100.percent)
		width(100.percent)
		overflowX(Overflow.Auto)
	}
}
