package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ItemNameComponent.Companion.ItemNameComponentSerializer::class)
data class ItemNameComponent(
	val components: ChatComponents,
) : Component() {
	companion object {
		object ItemNameComponentSerializer : InlineSerializer<ItemNameComponent, ChatComponents>(
			ToStringSerializer { asString() },
			ItemNameComponent::components
		)
	}
}

fun ComponentsScope.itemName(components: ChatComponents) = apply { this[ItemComponentTypes.ITEM_NAME] = ItemNameComponent(components) }
fun ComponentsScope.itemName(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
	itemName(textComponent(text, color, block))
