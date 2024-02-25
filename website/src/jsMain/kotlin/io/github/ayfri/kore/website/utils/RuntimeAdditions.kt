package io.github.ayfri.kore.website.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun loadPrism() = LaunchedEffect(Unit) {
	js("window.Prism.highlightAll()").unsafeCast<Unit>()
}
