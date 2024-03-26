package io.github.ayfri.kore.website.components.doc

data class DocArticle(
	val path: String,
	val date: String,
	val title: String,
	val desc: String,
	val navTitle: String,
	val keywords: List<String>,
	val dateModified: String,
	val slugs: List<String>,
)
