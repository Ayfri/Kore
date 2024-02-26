package io.github.ayfri.kore.website.components.layouts

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobwebx.markdown.markdown
import io.github.ayfri.kore.website.components.common.setDescription

@Composable
fun MarkdownLayout(content: @Composable () -> Unit) {
	val context = rememberPageContext()
	val markdownData = context.markdown!!.frontMatter

	PageLayout(markdownData["nav-title"]?.get(0) ?: "Untitled") {
		if (markdownData["description"] != null) {
			setDescription(markdownData["description"]!![0])
		}

		content()
	}
}
