package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
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

@Serializable(WrittenPage.Companion.WrittenPageSerializer::class)
data class WrittenPage(
	var raw: ChatComponents,
	var filtered: ChatComponents? = null,
	var single: Boolean = true,
) {
	companion object {
		object WrittenPageSerializer : KSerializer<WrittenPage> {
			override val descriptor = buildClassSerialDescriptor("WrittenPage") {
				element<ChatComponents>("raw")
				element<ChatComponents?>("filtered")
			}

			override fun deserialize(decoder: Decoder) = error("Page deserialization is not supported.")
			override fun serialize(encoder: Encoder, value: WrittenPage) {
				if (value.filtered != null || !value.single) {
					encoder.encodeStructure(descriptor) {
						encodeSerializableElement(
							descriptor,
							0,
							ChatComponents.Companion.ChatComponentsEscapedSerializer,
							value.raw
						)
						if (value.filtered != null) encodeSerializableElement(
							descriptor,
							1,
							ChatComponents.Companion.ChatComponentsEscapedSerializer,
							value.filtered!!
						)
					}
				} else {
					encoder.encodeSerializableValue(ChatComponents.Companion.ChatComponentsEscapedSerializer, value.raw)
				}
			}
		}
	}
}

private object WrittenPagesSerializer : KSerializer<List<WrittenPage>> {
	override val descriptor = buildClassSerialDescriptor("Pages") {
		element<List<WrittenPage>>("pages")
	}

	override fun deserialize(decoder: Decoder) = error("Pages deserialization is not supported.")
	override fun serialize(encoder: Encoder, value: List<WrittenPage>) {
		var values = value
		if (values.size > 1) values = values.map { it.copy(single = false) }
		encoder.encodeSerializableValue(ListSerializer(WrittenPage.serializer()), values)
	}
}

@Serializable
data class WrittenBookContentsComponent(
	@Serializable(with = WrittenPagesSerializer::class)
	var pages: List<WrittenPage>,
	var title: WrittenPage,
	var author: String,
	var generation: Int,
	var resolved: Boolean,
) : Component()

fun ComponentsScope.writtenBookContent(
	pages: List<WrittenPage>,
	title: WrittenPage,
	author: String,
	generation: Int,
	resolved: Boolean,
) = apply { this[ItemComponentTypes.WRITTEN_BOOK_CONTENT] = WrittenBookContentsComponent(pages, title, author, generation, resolved) }

fun ComponentsScope.writtenBookContent(
	title: WrittenPage,
	author: String,
	generation: Int = 0,
	resolved: Boolean = false,
	block: WrittenBookContentsComponent.() -> Unit,
) = apply {
	components["written_book_content"] =
		WrittenBookContentsComponent(emptyList(), title, author, generation, resolved).apply(block)
}

fun ComponentsScope.writtenBookContent(
	title: ChatComponents,
	author: String,
	generation: Int = 0,
	resolved: Boolean = false,
	block: WrittenBookContentsComponent.() -> Unit,
) = apply {
	components["written_book_content"] =
		WrittenBookContentsComponent(emptyList(), WrittenPage(title), author, generation, resolved).apply(block)
}

fun ComponentsScope.writtenBookContent(
	title: String,
	author: String,
	generation: Int = 0,
	resolved: Boolean = false,
	block: WrittenBookContentsComponent.() -> Unit,
) = apply {
	components["written_book_content"] =
		WrittenBookContentsComponent(emptyList(), WrittenPage(textComponent(title)), author, generation, resolved).apply(block)
}

fun WrittenBookContentsComponent.page(text: ChatComponents, filtered: ChatComponents? = null) = apply {
	pages += WrittenPage(text, filtered)
}
