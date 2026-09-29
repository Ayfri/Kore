package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

/** A book page or title: [raw] text, with a [filtered] version shown to players who turned chat filtering on. */
@Serializable(BookPage.Companion.BookPageSerializer::class)
data class BookPage<T>(var raw: T, var filtered: T? = null) {
	companion object {
		/** Writes a page as its bare [raw] text, or as `{raw, filtered}` when it has a [filtered] version. */
		class BookPageSerializer<T>(private val text: KSerializer<T>) : KSerializer<BookPage<T>> {
			override val descriptor = buildClassSerialDescriptor("BookPage", text.descriptor) {
				element("raw", text.descriptor)
				element("filtered", text.descriptor, isOptional = true)
			}

			/** Always writes the `{raw, filtered}` object. */
			val asObject = object : KSerializer<BookPage<T>> {
				override val descriptor get() = this@BookPageSerializer.descriptor

				override fun deserialize(decoder: Decoder) = this@BookPageSerializer.deserialize(decoder)

				override fun serialize(encoder: Encoder, value: BookPage<T>) = encoder.encodeStructure(descriptor) {
					encodeSerializableElement(descriptor, 0, text, value.raw)
					value.filtered?.let { encodeSerializableElement(descriptor, 1, text, it) }
				}
			}

			override fun deserialize(decoder: Decoder): BookPage<T> = error("Page deserialization is not supported.")

			override fun serialize(encoder: Encoder, value: BookPage<T>) = when (value.filtered) {
				null -> encoder.encodeSerializableValue(text, value.raw)
				else -> asObject.serialize(encoder, value)
			}
		}
	}
}

typealias WritablePage = BookPage<String>
typealias WrittenPage = BookPage<ChatComponents>

/** Writes a lone page like [BookPage.Companion.BookPageSerializer] and several as objects, since an NBT list can't mix text and objects. */
open class BookPagesSerializer<T>(text: KSerializer<T>) : KSerializer<List<BookPage<T>>> {
	private val page = BookPage.Companion.BookPageSerializer(text)
	private val pages = ListSerializer(page)
	private val objects = ListSerializer(page.asObject)

	override val descriptor = pages.descriptor

	override fun deserialize(decoder: Decoder): List<BookPage<T>> = error("Pages deserialization is not supported.")

	override fun serialize(encoder: Encoder, value: List<BookPage<T>>) =
		if (value.size > 1) encoder.encodeSerializableValue(objects, value) else encoder.encodeSerializableValue(pages, value)
}

data object WritablePagesSerializer : BookPagesSerializer<String>(String.serializer())
data object WrittenPagesSerializer : BookPagesSerializer<ChatComponents>(ChatComponents.serializer())
