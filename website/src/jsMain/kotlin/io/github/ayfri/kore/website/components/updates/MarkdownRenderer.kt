package io.github.ayfri.kore.website.components.updates

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.css.*
import io.github.ayfri.kore.website.GlobalStyle
import io.github.ayfri.kore.website.components.common.CodeBlock
import io.github.ayfri.kore.website.externals.MarkedToken
import io.github.ayfri.kore.website.externals.lexer
import io.github.ayfri.kore.website.externals.parser
import io.github.ayfri.kore.website.utils.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.dom.Div

/** A slice of a changelog: either a run of rendered markdown, or a fenced code block handed to [CodeBlock]. */
private sealed interface ReleaseBlock {
	data class Markup(val html: String) : ReleaseBlock
	data class Code(val text: String, val lang: String?) : ReleaseBlock
}

/**
 * Lexes [markdown] and splits it around fenced code blocks, so those can be rendered as real [CodeBlock] components
 * while everything else stays a single `marked` render pass.
 */
private fun parseReleaseBlocks(markdown: String): List<ReleaseBlock> {
	val blocks = mutableListOf<ReleaseBlock>()
	val pendingMarkup = mutableListOf<MarkedToken>()

	fun flushMarkup() {
		if (pendingMarkup.isEmpty()) return
		blocks += ReleaseBlock.Markup(parser(pendingMarkup.toTypedArray()))
		pendingMarkup.clear()
	}

	lexer(markdown).forEach { markedToken ->
		if (markedToken.type != "code") {
			pendingMarkup += markedToken
			return@forEach
		}

		flushMarkup()
		blocks += ReleaseBlock.Code(markedToken.text, markedToken.lang?.substringBefore(' ')?.ifBlank { null })
	}
	flushMarkup()

	return blocks
}

@Composable
fun MarkdownRenderer(markdown: String, id: String) {
	val blocks = remember(markdown) {
		runCatching { parseReleaseBlocks(markdown) }
			.onFailure { console.error("Error rendering markdown:", it.message) }
			.getOrDefault(listOf(ReleaseBlock.Markup("Failed to render markdown content.")))
	}

	Div({
		id(id)
		classes(MarkdownRendererStyle.container)
	}) {
		blocks.forEach { block ->
			when (block) {
				is ReleaseBlock.Markup -> Div({
					ref {
						it.innerHTML = block.html
						onDispose {}
					}
				})

				is ReleaseBlock.Code -> CodeBlock(block.text, block.lang)
			}
		}
	}

	// Scoped to this changelog: the page-wide `loadPrism` pass would redo the whole document once per release.
	highlightCodeIn(id, markdown)
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
			borderRadius(GlobalStyle.roundingButton)
			boxShadow(0.px, 2.px, 6.px, 0.px, rgba(0, 0, 0, 0.2))
			height(auto)
			maxWidth(100.percent)
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
