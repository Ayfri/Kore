package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:writable_book_content` item component, which stores the pages of a book and quill (editable text).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#writable_book_content
 */
@Serializable
data class WritableBookContentsComponent(@Serializable(WritablePagesSerializer::class) var pages: List<WritablePage>) : Component()

/** Stores the pages of a book and quill (editable text). */
fun ComponentsScope.writableBookContent(pages: List<WritablePage>) =
	apply { this[ItemComponentTypes.WRITABLE_BOOK_CONTENT] = WritableBookContentsComponent(pages) }

fun ComponentsScope.writableBookContent(vararg pages: WritablePage) =
	apply { this[ItemComponentTypes.WRITABLE_BOOK_CONTENT] = WritableBookContentsComponent(pages.toList()) }

fun ComponentsScope.writableBookContent(block: WritableBookContentsComponent.() -> Unit) =
	apply { this[ItemComponentTypes.WRITABLE_BOOK_CONTENT] = WritableBookContentsComponent(emptyList()).apply(block) }

fun WritableBookContentsComponent.page(text: String, filtered: String? = null) = apply {
	pages += WritablePage(text, filtered)
}
