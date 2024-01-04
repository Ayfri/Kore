package io.github.ayfri.kore.website.components.layouts

import androidx.compose.runtime.Composable

@Composable
fun MarkdownLayout(title: String, content: @Composable () -> Unit) {
	PageLayout(title) {
		content()
	}
}
