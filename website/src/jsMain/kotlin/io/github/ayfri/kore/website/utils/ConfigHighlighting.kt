package io.github.ayfri.kore.website.utils

import io.github.ayfri.kore.website.externals.Prism
import io.github.ayfri.kore.website.externals.grammar
import io.github.ayfri.kore.website.externals.set
import io.github.ayfri.kore.website.externals.token
import kotlin.js.RegExp

/** Registers minimal `groovy`, `xml` and `yaml` grammars, enough for build files and config snippets. */
fun initConfigHighlighting() {
	Prism.languages["groovy"] = grammar(
		"comment" to token(RegExp("//.*|/\\*[\\s\\S]*?\\*/"), greedy = true),
		"string" to token(RegExp("'''[\\s\\S]*?'''|\"\"\"[\\s\\S]*?\"\"\"|'(?:\\\\.|[^\\\\'\\r\\n])*'|\"(?:\\\\.|[^\\\\\"\\r\\n])*\""), greedy = true),
		"boolean" to RegExp("\\b(?:true|false)\\b"),
		"keyword" to RegExp("\\b(?:as|def|else|for|if|import|in|new|null|return|while)\\b"),
		// Groovy calls drop their parentheses, so `implementation 'x'` and `dependencies {` are calls too.
		"function" to RegExp("\\b[a-z_]\\w*(?=\\s*[({'\"])"),
		"number" to RegExp("\\b\\d+(?:\\.\\d+)?\\b"),
		"operator" to RegExp("[=+\\-*/<>!]=?"),
		"punctuation" to RegExp("[{}()\\[\\],.;:]"),
	)

	Prism.languages["xml"] = grammar(
		"comment" to token(RegExp("<!--[\\s\\S]*?-->"), greedy = true),
		"prolog" to token(RegExp("<\\?[\\s\\S]+?\\?>"), alias = "comment"),
		"tag" to token(
			RegExp("</?[\\w:.-]+(?:\\s+[\\w:.-]+(?:\\s*=\\s*(?:\"[^\"]*\"|'[^']*'))?)*\\s*/?>"),
			greedy = true,
			inside = grammar(
				"attr-value" to token(RegExp("(=\\s*)(?:\"[^\"]*\"|'[^']*')"), lookbehind = true, alias = "string"),
				"punctuation" to RegExp("^</?|/?>$|="),
				"tag-name" to token(RegExp("^[\\w:.-]+"), alias = "property"),
				"attr-name" to token(RegExp("[\\w:.-]+"), alias = "keyword"),
			),
		),
		"entity" to token(RegExp("&#?\\w+;"), alias = "constant"),
	)

	Prism.languages["yaml"] = grammar(
		"string" to token(RegExp("\"(?:\\\\.|[^\\\\\"\\r\\n])*\"|'(?:''|[^'\\r\\n])*'"), greedy = true),
		"comment" to token(RegExp("(^|\\s)#.*"), lookbehind = true, greedy = true),
		"key" to token(RegExp("(^[ \\t]*(?:-[ \\t]+)?)[^\\s#:'\"-][^\\r\\n:#]*?(?=[ \\t]*:(?:\\s|$))", "m"), lookbehind = true, alias = "property"),
		"boolean" to RegExp("\\b(?:true|false|yes|no|on|off)\\b"),
		"null" to token(RegExp("(^|[\\s:\\[,])(?:null|~)(?=\\s|$|[\\],])"), lookbehind = true, alias = "keyword"),
		// Only standalone numbers, so versions like `2.8.0-26.1.2` inside a coordinate stay plain text.
		"number" to token(RegExp("(^|[\\s:\\[,])[+-]?\\d+(?:\\.\\d+)?(?=\\s*$|\\s*[\\],#])", "m"), lookbehind = true),
		"punctuation" to token(RegExp("(^[ \\t]*)-(?=\\s)|:(?=\\s|$)|[\\[\\]{},|>]", "m"), lookbehind = true),
	)
}
