package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.StringifiedNbt

@Serializable(with = LoreComponent.Companion.LoreComponentSerializer::class)
data class LoreComponent(
	var list: ChatComponents = ChatComponents(),
) : Component() {
	override fun isChatComponent() = true

	companion object {
		// Lore is always serialized as an SNBT string of the component list (e.g. `[{type:"text",text:"a"}]`), in both NBT and JSON.
		data object LoreComponentSerializer : KSerializer<LoreComponent> {
			override val descriptor = PrimitiveSerialDescriptor("LoreComponent", PrimitiveKind.STRING)
			override fun deserialize(decoder: Decoder) = error("LoreComponent cannot be deserialized.")
			override fun serialize(encoder: Encoder, value: LoreComponent) =
				encoder.encodeString(StringifiedNbt.encodeToString(value.list.toNbtList()))
		}
	}
}

fun ComponentsScope.lore(chatComponents: ChatComponents) = apply { this[ItemComponentTypes.LORE] = LoreComponent(chatComponents) }

fun ComponentsScope.lore(text: String = "", color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
	lore(textComponent(text, color, block))

fun ComponentsScope.lores(block: ChatComponents.() -> Unit) = lore(ChatComponents().apply(block))
