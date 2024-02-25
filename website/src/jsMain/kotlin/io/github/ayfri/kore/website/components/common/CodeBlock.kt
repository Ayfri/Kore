package io.github.ayfri.kore.website.components.common

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Code
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Text

@Composable
fun CodeBlock(text: String, lang: String? = null) {
	Pre {
		Code(attrs = {
			classes(lang?.let { "language-$it" } ?: "nohighlight", "line-numbers")
		}) {
			Text(text)
		}
	}
}
