package io.github.ayfri.kore.website.components.common

import androidx.compose.runtime.Composable
import io.github.ayfri.kore.website.externals.parse
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.P
import org.w3c.dom.HTMLParagraphElement

@Composable
fun Markdown(text: String, classes: String = "", block: (AttrsScope<HTMLParagraphElement>.() -> Unit)? = null) = P({
	ref {
		it.innerHTML = parse(text)
		onDispose {}
	}

	classes(classes)
	block?.invoke(this)
})