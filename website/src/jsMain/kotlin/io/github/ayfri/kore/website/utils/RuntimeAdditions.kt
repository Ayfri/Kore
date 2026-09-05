package io.github.ayfri.kore.website.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.ayfri.kore.website.externals.Prism
import kotlinx.browser.document

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
	Prism.highlightAll()
}

/** Highlights only the code blocks inside the element with [elementId], re-running whenever [key] changes. */
@Composable
fun highlightCodeIn(elementId: String, key: Any?) = LaunchedEffect(key) {
	prepareKotlinHighlighting()
	val root = document.getElementById(elementId) ?: return@LaunchedEffect
	Prism.highlightAllUnder(root)
}
