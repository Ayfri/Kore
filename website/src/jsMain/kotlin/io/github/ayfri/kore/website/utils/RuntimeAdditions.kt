package io.github.ayfri.kore.website.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.ayfri.kore.website.externals.Prism
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.MouseEvent
import kotlin.math.floor

private var highlightingReady = false

/** Every nested tab or 4-space run gets wrapped so CSS can draw an IntelliJ-like guide on it, the top level stays bare. */
private val indentUnit = Regex("\t| {4}")
private val leadingIndent = Regex("(^|\n)(\t| {4})([\t ]*)")

private fun prepareHighlighting() {
	if (highlightingReady) return
	initKotlinHighlighting()
	initConfigHighlighting()
	initMCFunctionHighlighting()

	Prism.hooks.add("before-insert") { env ->
		env.highlightedCode = env.highlightedCode.replace(leadingIndent) { match ->
			val (lineStart, topLevel, nested) = match.destructured
			lineStart + topLevel + nested.replace(indentUnit) { """<span class="token indent-guide">${it.value}</span>""" }
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
		if (y < 0 || y >= contentHeight) pre.style.removeProperty("--hover-y")
		else pre.style.setProperty("--hover-y", "${paddingTop + floor(y / lineHeight) * lineHeight}px")
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
