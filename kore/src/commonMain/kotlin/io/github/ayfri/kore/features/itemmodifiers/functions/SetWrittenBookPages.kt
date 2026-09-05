package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.item.WrittenPage
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.types.Mode
import io.github.ayfri.kore.features.itemmodifiers.types.ModeHandler
import io.github.ayfri.kore.features.predicates.PredicateAsList
import kotlinx.serialization.Serializable

/**
 * Sets the pages of a written book. Mirrors `minecraft:set_written_book_pages`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetWrittenBookPages(
	override var conditions: PredicateAsList? = null,
	var pages: List<WrittenPage> = emptyList(),
) : ItemFunction(), ModeHandler {
	@Serializable
	override var mode: Mode = Mode.REPLACE_ALL

	@Serializable
	override var offset: Int? = null

	@Serializable
	override var size: Int? = null
}

/** Add a `set_written_book_pages` step. */
fun ItemModifier.setWrittenBookPages(
	pages: List<WrittenPage> = emptyList(),
	block: SetWrittenBookPages.() -> Unit = {},
) = SetWrittenBookPages(pages = pages).also {
	this.modifiers += it.apply(block)
}

/** Append a single page to the builder list. */
fun SetWrittenBookPages.page(text: ChatComponents, filtered: ChatComponents? = null) = apply {
	pages += WrittenPage(text, filtered)
}

/** Append a single page to the builder list. */
fun SetWrittenBookPages.page(text: String, color: Color? = null, filtered: String? = null, textBlock: PlainTextComponent.() -> Unit = {}) =
	apply {
		pages += WrittenPage(textComponent(text, color, textBlock), filtered?.let(::textComponent))
	}
