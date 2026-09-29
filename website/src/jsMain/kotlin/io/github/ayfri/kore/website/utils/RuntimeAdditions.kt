package io.github.ayfri.kore.website.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.ayfri.kore.website.HoverY
import io.github.ayfri.kore.website.externals.Prism
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.MouseEvent
import kotlin.math.floor

private var highlightingReady = false

/** Every nested tab or indent unit gets wrapped so CSS can draw an IntelliJ-like guide on it, the top level stays bare. */
private class IndentGuides(spaces: Int) {
	val leading = Regex("(^|\n)(\t| {$spaces})([\t ]*)")
	val unit = Regex("\t| {$spaces}")
}

private val fourSpaceGuides = IndentGuides(4)
private val twoSpaceGuides = IndentGuides(2)

/** A line indented by exactly two spaces marks a 2-space block, whose guides then follow every level rather than every other one. */
private val twoSpaceLine = Regex("^ {2}\\S", RegexOption.MULTILINE)

private fun prepareHighlighting() {
	if (highlightingReady) return
	initKotlinHighlighting()
	initConfigHighlighting()
	initMCFunctionHighlighting()

	Prism.hooks.add("before-insert") { env ->
		val guides = if (twoSpaceLine.containsMatchIn(env.code)) twoSpaceGuides else fourSpaceGuides
		env.highlightedCode = env.highlightedCode.replace(guides.leading) { match ->
			val (lineStart, topLevel, nested) = match.destructured
			lineStart + topLevel + nested.replace(guides.unit) { """<span class="token indent-guide">${it.value}</span>""" }
		}
	}

	// Prism never wraps lines, so the hovered line is drawn as a background band placed from the cursor position.
	document.addEventListener("mousemove", { event ->
		event as MouseEvent
		val pre = (event.target as? Element)?.closest("pre[class*='language-']") as? HTMLElement ?: return@addEventListener
		val style = window.getComputedStyle(pre)
		val lineHeight = style.lineHeight.removeSuffix("px").toDoubleOrNull() ?: return@addEventListener
		val paddingTop = style.paddingTop.removeSuffix("px").toDouble()
		val contentHeight = pre.scrollHeight - paddingTop - style.paddingBottom.removeSuffix("px").toDouble()
		val y = event.clientY - pre.getBoundingClientRect().top - pre.clientTop + pre.scrollTop - paddingTop
		if (y < 0 || y >= contentHeight) pre.style.removeProperty("--${HoverY.name}")
		else pre.style.setProperty("--${HoverY.name}", "${paddingTop + floor(y / lineHeight) * lineHeight}px")
	})

	highlightingReady = true
}

/** Highlights every code block of the page, once it is mounted. */
@Composable
fun loadPrism() = LaunchedEffect(Unit) {
	prepareHighlighting()
	Prism.highlightAll()
}

/** Highlights only the code blocks inside the element with [elementId], re-running whenever [key] changes. */
@Composable
fun highlightCodeIn(elementId: String, key: Any?) = LaunchedEffect(key) {
	prepareHighlighting()
	val root = document.getElementById(elementId) ?: return@LaunchedEffect
	Prism.highlightAllUnder(root)
}
