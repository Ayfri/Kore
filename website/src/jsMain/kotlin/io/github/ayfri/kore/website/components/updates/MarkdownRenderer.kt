package io.github.ayfri.kore.website.components.updates

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.utils.alpha
import io.github.ayfri.kore.website.utils.loadPrism
import io.github.ayfri.kore.website.utils.marginY
import io.github.ayfri.kore.website.utils.mdMax
import io.github.ayfri.kore.website.utils.paddingY
import io.github.ayfri.kore.website.utils.smMax
import io.github.ayfri.kore.website.utils.transition
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLElement
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.clear
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.get

@Composable
fun MarkdownRenderer(markdown: String, id: String) {
	Style(MarkdownRendererStyle)
	loadPrism()

	// Create a wrapper div with the specified ID
	Div({
		id(id)
		classes(MarkdownRendererStyle.container)
	})

	// Load Prism.js for syntax highlighting
	LaunchedEffect(markdown) {
		// Wait a brief moment to ensure the DOM is ready
		val container = document.getElementById(id) as? HTMLDivElement
		if (container != null) {
			container.clear()

			try {
				// Parse markdown to HTML using marked.js
				val html = js("marked.parse(markdown)").unsafeCast<String>()
				container.innerHTML = html

				// Process code blocks for syntax highlighting
				val codeBlocks = container.querySelectorAll("pre code")
				for (i in 0 until codeBlocks.length) {
					val codeBlock = codeBlocks[i] as? HTMLElement ?: continue
					val parent = codeBlock.parentElement as? Element ?: continue

					// Add language class for Prism
					val language = codeBlock.className.split("-").getOrNull(1)
					if (language != null) {
						parent.className = "language-$language"
						codeBlock.className = "language-$language"
					}
				}
			} catch (e: Exception) {
				console.error("Error rendering markdown:", e.message)
				container.textContent = "Failed to render markdown content."
			}
		}
	}
}

object MarkdownRendererStyle : StyleSheet() {
	val container by style {
		backgroundColor(GlobalStyle.backgroundColor.alpha(0.5))
		borderRadius(GlobalStyle.roundingButton)
		color(GlobalStyle.textColor)
		fontSize(1.cssRem)
		lineHeight(1.6.number)
		maxWidth(100.percent)
		overflowX(Overflow.Auto)
		padding(0.5.cssRem)
		boxShadow(0.px, 2.px, 6.px, 0.px, rgba(0, 0, 0, 0.1))

		// Add min-height to ensure content appears
		minHeight(2.cssRem)

		"h1, h2, h3, h4, h5, h6" style {
			marginY(0.75.cssRem)
			fontWeight(600)
		}

		"h1" style {
			fontSize(1.8.cssRem)
			borderBottom(1.px, LineStyle.Solid, GlobalStyle.borderColor)
			paddingY(0.5.cssRem)
		}

		"h2" style {
			fontSize(1.5.cssRem)
			borderBottom(1.px, LineStyle.Solid, GlobalStyle.borderColor.alpha(0.5))
			paddingBottom(0.3.cssRem)
		}

		"h3" style {
			fontSize(1.3.cssRem)
		}

		"p" style {
			marginY(0.8.cssRem)
		}

		"a" style {
			color(GlobalStyle.linkColor)
			textDecorationLine(TextDecorationLine.None)
			transition(0.2.s, "color", "text-decoration")

			hover(self) style {
				color(GlobalStyle.linkColorHover)
				textDecorationLine(TextDecorationLine.Underline)
			}
		}

		"pre" style {
			backgroundColor(GlobalStyle.secondaryBackgroundColor)
			borderRadius(GlobalStyle.roundingButton)
			marginY(1.cssRem)
			padding(1.cssRem)
			overflowX(Overflow.Auto)
			border(1.px, LineStyle.Solid, GlobalStyle.tertiaryBackgroundColor.alpha(0.3))
		}

		"code" style {
			fontFamily("Consolas", "Monaco", "Andale Mono", "Ubuntu Mono", "monospace")
			fontSize(0.9.cssRem)
		}

		"code:not([class*='language-'])" style {
			backgroundColor(GlobalStyle.secondaryBackgroundColor)
			borderRadius(GlobalStyle.roundingButton)
			padding(0.2.cssRem, 0.4.cssRem)
			color(GlobalStyle.linkColorHover)
		}

		"ul, ol" style {
			marginY(1.cssRem)
			paddingLeft(2.cssRem)
		}

		"li" style {
			marginY(0.4.cssRem)
		}

		"blockquote" style {
			borderLeft(4.px, LineStyle.Solid, GlobalStyle.altTextColor)
			marginLeft(0.px)
			paddingLeft(1.cssRem)
			color(GlobalStyle.altTextColor)
			backgroundColor(GlobalStyle.secondaryBackgroundColor.alpha(0.3))
			borderRadius(0.px, GlobalStyle.roundingButton, GlobalStyle.roundingButton, 0.px)
			padding(0.8.cssRem)
			paddingLeft(1.cssRem)
		}

		"hr" style {
			border(0.px)
			borderTop(1.px, LineStyle.Solid, GlobalStyle.borderColor)
			marginY(1.5.cssRem)
		}

		"img" style {
			maxWidth(100.percent)
			borderRadius(GlobalStyle.roundingButton)
			boxShadow(0.px, 2.px, 6.px, 0.px, rgba(0, 0, 0, 0.2))
		}

		"table" style {
			borderCollapse(BorderCollapse.Collapse)
			width(100.percent)
			marginY(1.cssRem)
			borderRadius(GlobalStyle.roundingButton)
			overflow(Overflow.Hidden)
		}

		"th, td" style {
			border(1.px, LineStyle.Solid, GlobalStyle.borderColor)
			padding(0.6.cssRem)
			textAlign(TextAlign.Left)
		}

		"th" style {
			backgroundColor(GlobalStyle.secondaryBackgroundColor)
			fontWeight(600)
		}

		"tr:nth-child(even)" style {
			backgroundColor(GlobalStyle.secondaryBackgroundColor.alpha(0.3))
		}

		mdMax(self) {
			fontSize(0.95.cssRem)
			padding(1.cssRem)
		}

		smMax(self) {
			padding(0.8.cssRem)
		}
	}
}
