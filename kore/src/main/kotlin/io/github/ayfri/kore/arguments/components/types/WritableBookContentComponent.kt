package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

@Serializable(WritablePage.Companion.WritablePageSerializer::class)
data class WritablePage(
	var raw: String,
	var filtered: String? = null,
	var single: Boolean = true,
) {
	companion object {
		object WritablePageSerializer : KSerializer<WritablePage> {
			override val descriptor = buildClassSerialDescriptor("Page") {
				element<String>("raw")
				element<String>("filtered")
			}

			override fun deserialize(decoder: Decoder) = error("Page deserialization is not supported.")
			override fun serialize(encoder: Encoder, value: WritablePage) {
				if (value.filtered != null || !value.single) {
					encoder.encodeStructure(descriptor) {
						encodeStringElement(descriptor, 0, value.raw)
						if (value.filtered != null) encodeStringElement(descriptor, 1, value.filtered!!)
					}
				} else {
					encoder.encodeString(value.raw)
				}
			}
		}
	}
}

object WritablePagesSerializer : KSerializer<List<WritablePage>> {
	override val descriptor = buildClassSerialDescriptor("Pages") {
		element<List<WritablePage>>("pages")
	}

	override fun deserialize(decoder: Decoder) = error("Pages deserialization is not supported.")
	override fun serialize(encoder: Encoder, value: List<WritablePage>) {
		var values = value
		if (values.size > 1) values = values.map { it.copy(single = false) }

		encoder.encodeSerializableValue(ListSerializer(WritablePage.serializer()), values)
	}
}

@Serializable
data class WritableBookContentsComponent(@Serializable(WritablePagesSerializer::class) var pages: List<WritablePage>) : Component()

fun ComponentsScope.writableBookContent(pages: List<WritablePage>) =
	apply { this[ItemComponentTypes.WRITABLE_BOOK_CONTENT] = WritableBookContentsComponent(pages) }

fun ComponentsScope.writableBookContent(vararg pages: WritablePage) =
	apply { this[ItemComponentTypes.WRITABLE_BOOK_CONTENT] = WritableBookContentsComponent(pages.toList()) }

fun ComponentsScope.writableBookContent(block: WritableBookContentsComponent.() -> Unit) =
	apply { this[ItemComponentTypes.WRITABLE_BOOK_CONTENT] = WritableBookContentsComponent(emptyList()).apply(block) }

fun WritableBookContentsComponent.page(text: String, filtered: String? = null) = apply {
	pages += WritablePage(text, filtered)
}
