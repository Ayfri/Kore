package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.components.item.WritablePage
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable
/**
 * Sets title/author/generation for written books. Mirrors `minecraft:set_book_cover`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetBookCover(
	override var conditions: PredicateAsList? = null,
	/** Plain text up to 32 characters, with an optional filtered version, the game doesn't take a text component here. */
	var title: WritablePage? = null,
	var author: String? = null,
	var generation: Int? = null,
) : ItemFunction()

/** Add a `set_book_cover` step with a [WritablePage] title, to also give a filtered version. */
fun ItemModifier.setBookCover(
	title: WritablePage? = null,
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
) = setBookCover(WritablePage(title), author, generation, block)
