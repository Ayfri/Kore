package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.components.types.Component
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNamingStrategy
import net.benwoodworth.knbt.NbtEncoder

@OptIn(ExperimentalSerializationApi::class)
val jsonSerializer = Json {
	prettyPrint = false
	encodeDefaults = false
	classDiscriminatorMode = ClassDiscriminatorMode.NONE
	namingStrategy = JsonNamingStrategy.SnakeCase
}

/**
 * List of all the Components that are chat components and thus must be quoted.
 * Modify this list if you want to add a new chat component Component.
 *
 * See [ChatComponentsEscapedSerializer][io.github.ayfri.kore.arguments.chatcomponents.ChatComponents.Companion.ChatComponentsEscapedSerializer] for understanding how it's serialized.
 */
val CHAT_COMPONENTS_COMPONENTS_TYPES = mutableListOf(
	"custom_name",
	"item_name",
	"lore",
	"written_book_content",
)


object ComponentsSerializer : KSerializer<ComponentsScope> {
	override val descriptor = buildClassSerialDescriptor("Components") {
		element<Map<String, Component>>("components")
	}

	override fun deserialize(decoder: Decoder) = error("Components deserialization is not supported.")

	override fun serialize(encoder: Encoder, value: ComponentsScope) = when (encoder) {
		is NbtEncoder -> encoder.encodeNbtTag(value.asNbt())
		is JsonEncoder -> encoder.encodeJsonElement(value.asJson())
		else -> error("This serializer can only be used with Nbt or Json")
	}
}
