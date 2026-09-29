package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:written_book_content` item component, which stores the signed pages, title, and author of a written book.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#written_book_content
 */
@Serializable
data class WrittenBookContentsComponent(
	@Serializable(with = WrittenPagesSerializer::class)
	var pages: List<WrittenPage>,
	/** Plain text up to 32 characters, with an optional filtered version, the game doesn't take a text component here. */
	var title: WritablePage,
	var author: String,
	var generation: Int,
	var resolved: Boolean,
) : Component()

/** Stores the signed pages, title, and author of a written book. */
fun ComponentsScope.writtenBookContent(
	pages: List<WrittenPage>,
	title: WritablePage,
	author: String,
	generation: Int,
	resolved: Boolean,
) = apply { this[ItemComponentTypes.WRITTEN_BOOK_CONTENT] = WrittenBookContentsComponent(pages, title, author, generation, resolved) }

fun ComponentsScope.writtenBookContent(
	title: WritablePage,
	author: String,
	generation: Int = 0,
	resolved: Boolean = false,
	block: WrittenBookContentsComponent.() -> Unit,
) = apply {
	components["written_book_content"] =
		WrittenBookContentsComponent(emptyList(), title, author, generation, resolved).apply(block)
}

fun ComponentsScope.writtenBookContent(
	title: String,
	author: String,
	generation: Int = 0,
	resolved: Boolean = false,
	block: WrittenBookContentsComponent.() -> Unit,
) = writtenBookContent(WritablePage(title), author, generation, resolved, block)

fun WrittenBookContentsComponent.page(text: ChatComponents, filtered: ChatComponents? = null) = apply {
	pages += WrittenPage(text, filtered)
}
