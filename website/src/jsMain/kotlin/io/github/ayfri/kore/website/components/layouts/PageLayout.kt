package io.github.ayfri.kore.website.components.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.browser.document

@Composable
fun PageLayout(title: String, content: @Composable () -> Unit) {
	LaunchedEffect(title) {
		document.title = "Kobweb - $title"
	}

	content()
}
