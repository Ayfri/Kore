package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.NbtEncoder
import net.benwoodworth.knbt.NbtTag

/**
 * Represents the `minecraft:lore` item component, which adds tooltip lines below the item name for descriptions or flavor text.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#lore
 */
@Serializable(with = LoreComponent.Companion.LoreComponentSerializer::class)
data class LoreComponent(
	var list: ChatComponents = ChatComponents(),
) : Component() {
	companion object {
		/** Always a list, even for a single line, with every line as a compound as soon as one isn't plain text, since NBT lists hold one type. */
		data object LoreComponentSerializer : KSerializer<LoreComponent> {
			override val descriptor = ListSerializer(NbtTag.serializer()).descriptor
			override fun deserialize(decoder: Decoder) = error("LoreComponent cannot be deserialized.")
			override fun serialize(encoder: Encoder, value: LoreComponent) = when (encoder) {
				is NbtEncoder -> encoder.encodeNbtTag(value.list.toNbtList())
				else -> encoder.encodeSerializableValue(NbtAsJsonSerializer, value.list.toNbtList())
			}
		}
	}
}

/** Adds tooltip lines below the item name for descriptions or flavor text. */
fun ComponentsScope.lore(chatComponents: ChatComponents) = apply { this[ItemComponentTypes.LORE] = LoreComponent(chatComponents) }

fun ComponentsScope.lore(text: String = "", color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
	lore(textComponent(text, color, block))

fun ComponentsScope.lores(block: ChatComponents.() -> Unit) = lore(ChatComponents().apply(block))
