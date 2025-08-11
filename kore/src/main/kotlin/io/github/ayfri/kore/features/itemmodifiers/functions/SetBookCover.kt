package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.components.item.WrittenPage
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList

/**
 * Sets title/author/generation for written books. Mirrors `minecraft:set_book_cover`.
 *
 * Docs: https://kore.ayfri.com/docs/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
data class SetBookCover(
	override var conditions: PredicateAsList? = null,
	var title: WrittenPage? = null,
	var author: String? = null,
	var generation: Int? = null,
) : ItemFunction()

/** Add a `set_book_cover` step with a fully constructed [WrittenPage] title. */
fun ItemModifier.setBookCover(
	title: WrittenPage? = null,
	author: String? = null,
	generation: Int? = null,
	block: SetBookCover.() -> Unit = {},
) {
	modifiers += SetBookCover(title = title, author = author, generation = generation).apply(block)
}

/** Title convenience overload from a plain string. */
fun ItemModifier.setBookCover(
	title: String,
	author: String? = null,
	generation: Int? = null,
	block: SetBookCover.() -> Unit = {},
) {
	modifiers += SetBookCover(title = WrittenPage(textComponent(title)), author = author, generation = generation).apply(block)
}

/** Title convenience overload from chat components. */
fun ItemModifier.setBookCover(
	title: ChatComponents,
	author: String? = null,
	generation: Int? = null,
	block: SetBookCover.() -> Unit = {},
) {
	modifiers += SetBookCover(title = WrittenPage(title), author = author, generation = generation).apply(block)
}
