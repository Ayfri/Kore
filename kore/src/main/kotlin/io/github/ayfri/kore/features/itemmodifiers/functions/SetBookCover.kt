package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.components.WrittenPage
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList

data class SetBookCover(
	override var conditions: PredicateAsList? = null,
	var title: WrittenPage? = null,
	var author: String? = null,
	var generation: Int? = null,
) : ItemFunction()

fun ItemModifier.setBookCover(
	title: WrittenPage? = null,
	author: String? = null,
	generation: Int? = null,
	block: SetBookCover.() -> Unit = {},
) {
	modifiers += SetBookCover(title = title, author = author, generation = generation).apply(block)
}

fun ItemModifier.setBookCover(
	title: String,
	author: String? = null,
	generation: Int? = null,
	block: SetBookCover.() -> Unit = {},
) {
	modifiers += SetBookCover(title = WrittenPage(textComponent(title)), author = author, generation = generation).apply(block)
}

fun ItemModifier.setBookCover(
	title: ChatComponents,
	author: String? = null,
	generation: Int? = null,
	block: SetBookCover.() -> Unit = {},
) {
	modifiers += SetBookCover(title = WrittenPage(title), author = author, generation = generation).apply(block)
}
