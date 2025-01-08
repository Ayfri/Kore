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
	val position: Int? = null,
)

val DocArticle.middleSlugs get() = slugs.drop(1).dropLast(1)
