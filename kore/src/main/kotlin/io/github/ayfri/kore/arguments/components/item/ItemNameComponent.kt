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
 * Represents the `minecraft:item_name` item component, which sets the item's base name (not italic, not removable in anvil).
 *
 * Unlike [CustomNameComponent], this name is not italic and cannot be cleared by a player.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#item_name
 */
@Serializable(with = ItemNameComponent.Companion.ItemNameComponentSerializer::class)
data class ItemNameComponent(val components: ChatComponents) : Component() {
	override fun isChatComponent() = true

	companion object {
		data object ItemNameComponentSerializer : InlineAutoSerializer<ItemNameComponent>(ItemNameComponent::class)
	}
}

/** Sets the item's base name (different from custom name; not italicized). */
fun ComponentsScope.itemName(components: ChatComponents) = apply { this[ItemComponentTypes.ITEM_NAME] = ItemNameComponent(components) }
fun ComponentsScope.itemName(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
	itemName(textComponent(text, color, block))
