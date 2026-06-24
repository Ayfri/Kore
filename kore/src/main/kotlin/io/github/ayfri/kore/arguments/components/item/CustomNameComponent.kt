package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:custom_name` item component, which sets a custom display name (a text component) for the item.
 *
 * Unlike [ItemNameComponent], this name is italicized and can be removed in an anvil.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#custom_name
 */
@Serializable(with = CustomNameComponent.Companion.CustomNameComponentSerializer::class)
data class CustomNameComponent(val component: ChatComponents) : Component() {
	override fun isChatComponent() = true

	companion object {
		data object CustomNameComponentSerializer : InlineAutoSerializer<CustomNameComponent>(CustomNameComponent::class)
	}
}

/** Sets a custom display name for the item (supports text components). */
fun ComponentsScope.customName(component: ChatComponents) = apply { this[ItemComponentTypes.CUSTOM_NAME] = CustomNameComponent(component) }
fun ComponentsScope.customName(text: String = "", color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
	customName(textComponent(text, color, block))
