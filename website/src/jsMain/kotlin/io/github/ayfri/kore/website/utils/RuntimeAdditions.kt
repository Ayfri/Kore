package io.github.ayfri.kore.website.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.browser.document
import org.w3c.dom.Element

private var kotlinHighlightingReady = false

private fun prepareKotlinHighlighting() {
	if (kotlinHighlightingReady) return
	initKotlinHighlighting()
	kotlinHighlightingReady = true
}

/** Highlights every code block of the page, once it is mounted. */
@Composable
fun loadPrism() = LaunchedEffect(Unit) {
	prepareKotlinHighlighting()
	js("window.Prism.highlightAll()").unsafeCast<Unit>()
}

/** Highlights only the code blocks inside the element with [elementId], re-running whenever [key] changes. */
@Composable
fun highlightCodeIn(elementId: String, key: Any?) = LaunchedEffect(key) {
	prepareKotlinHighlighting()
	val root: Element = document.getElementById(elementId) ?: return@LaunchedEffect
	js("window.Prism.highlightAllUnder(root)").unsafeCast<Unit>()
}
