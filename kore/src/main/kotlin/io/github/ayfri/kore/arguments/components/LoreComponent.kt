package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = LoreComponent.Companion.LoreComponentSerializer::class)
data class LoreComponent(
	var list: ChatComponents = ChatComponents(),
) : Component() {
	companion object {
		object LoreComponentSerializer : KSerializer<LoreComponent> {
			override val descriptor = ChatComponents.Companion.ChatComponentsSerializer.descriptor

			override fun deserialize(decoder: Decoder) = error("LoreComponent cannot be deserialized")

			override fun serialize(encoder: Encoder, value: LoreComponent) {
				encoder.encodeSerializableValue(ChatComponents.Companion.ChatComponentsAsListSerializer, value.list)
			}
		}
	}
}

fun Components.lore(chatComponents: ChatComponents) = apply { components["lore"] = LoreComponent(chatComponents) }

fun Components.lore(text: String = "", color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
	lore(textComponent(text, color, block))

fun Components.lores(block: ChatComponents.() -> Unit) = lore(ChatComponents().apply(block))
