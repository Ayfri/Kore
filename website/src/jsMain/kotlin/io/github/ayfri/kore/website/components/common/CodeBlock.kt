package io.github.ayfri.kore.website.components.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import io.github.ayfri.kore.website.utils.highlightCodeIn
import org.jetbrains.compose.web.dom.Code
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Pre
import org.jetbrains.compose.web.dom.Text

@Composable
fun CodeBlock(text: String, lang: String? = null, vararg classes: String) {
	Pre({
		classes(*classes)
	}) {
		Code(attrs = {
			classes(lang?.let { "language-$it" } ?: "nohighlight", "line-numbers")
		}) {
			Text(text)
		}
	}
}

class CodeTab(val name: String, val code: String, val language: String)

/** A [CodeBlock] of [code] in an element with the id [id], re-highlighted when [code] changes. */
@Composable
fun HighlightedCode(id: String, code: String, language: String) {
	Div({ id(id) }) { key(code) { CodeBlock(code, language) } }
	highlightCodeIn(id, code)
}

/** [Tabs] of one [HighlightedCode] per tab, each in an element with the id `$idPrefix-<name in lowercase>`. */
@Composable
fun CodeTabs(idPrefix: String, vararg tabs: CodeTab) {
	Tabs(tabs.map { tab -> Tab(tab.name) { HighlightedCode("$idPrefix-${tab.name.lowercase()}", tab.code, tab.language) } })
}
